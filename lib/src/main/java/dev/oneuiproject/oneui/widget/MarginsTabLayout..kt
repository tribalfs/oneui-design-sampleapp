@file:Suppress("MemberVisibilityCanBePrivate")

package dev.oneuiproject.oneui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.content.res.use
import com.google.android.material.tabs.TabLayout
import dev.oneuiproject.oneui.design.R
import dev.oneuiproject.oneui.ktx.getTabView
import dev.oneuiproject.oneui.utils.DeviceLayoutUtil.isDisplayTypeSub
import dev.oneuiproject.oneui.utils.DeviceLayoutUtil.isLandscape

/**
 * An extension of [TabLayout] that dynamically adjusts its width to fit the parent
 * by setting appropriate side margins. It ensures that the selected tab is always
 * visible by automatically scrolling to it.
 *
 * This class provides a mechanism to customize the tab dimensions through the
 * [setCustomTabDimen] method, allowing for flexible layout adjustments.
 */
open class MarginsTabLayout @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.tabStyle
) : TabLayout(mContext, attrs, defStyleAttr) {

    @JvmField
    internal var tabDimens: TabDimen? = null
    @JvmField
    internal var tabTextWidthsList: ArrayList<Float> = arrayListOf()
    @JvmField
    internal val defaultTabPadding = context.resources.getDimension(R.dimen.oui_tab_layout_default_tab_padding)

    private var sideMarginChanged = false
    @JvmField
    internal var sideMargin: Int = 0
    @JvmField
    internal var containerWidth: Int? = null

    @JvmField
    internal var depthStyle = DEPTH_TYPE_MAIN

    @JvmField
    internal var mRecalculateTextWidths = false

    init{
        context.obtainStyledAttributes(attrs, R.styleable.MarginsTabLayout).use{
            depthStyle = it.getInteger(R.styleable.MarginsTabLayout_seslDepthStyle, DEPTH_TYPE_MAIN)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (depthStyle == DEPTH_TYPE_SUB){
            super.seslSetSubTabStyle()
        }
    }

    override fun seslSetSubTabStyle() {
        if (depthStyle == DEPTH_TYPE_SUB) return
        super.seslSetSubTabStyle()
        depthStyle = DEPTH_TYPE_SUB
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateLayoutParams()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (containerWidth != null || calculateMarginsInternal()){
            updateLayoutParams()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == VISIBLE) {
            alpha = 0f
            animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(100)
                .start()
        }
        super.onVisibilityChanged(changedView, visibility)
    }


    override fun addTab(tab: Tab, position: Int, setSelected: Boolean) {
        mRecalculateTextWidths = true
        super.addTab(tab, position, setSelected)
    }

    override fun removeTab(tab: Tab) {
        mRecalculateTextWidths = true
        super.removeTab(tab)
    }

    private fun recalculateTextWidths() {
        tabTextWidthsList.clear()
        for (i in 0 until tabCount) {
            val tab = getTabAt(i)
            val viewGroup = getTabView(i) as? ViewGroup
            if (tab != null && viewGroup != null) {
                val textView = tab.seslGetTextView()
                val subTextView = tab.seslGetSubTextView()
                val tabTextWidth = textView?.paint?.measureText(textView.getText().toString())
                    ?.coerceAtLeast(subTextView?.paint?.measureText(textView.getText().toString()) ?: 0f)
                    ?: 0f
                tabTextWidthsList.add(tabTextWidth)
            }
        }
    }

    override fun onAttachedToWindow() {
        calculateMarginsInternal()
        setScrollPosition(selectedTabPosition, 0.0f, true)
        super.onAttachedToWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (isAttachedToWindow) {
            calculateMarginsInternal()
            setScrollPosition(selectedTabPosition, 0.0f, true)
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }


    private fun calculateMarginsInternal(): Boolean {
        val parentWidth = with(parent as ViewGroup) { width - paddingStart - paddingEnd }
        if (parentWidth != 0 && containerWidth != parentWidth) {
            containerWidth = parentWidth
            if (tabDimens == null) {
                tabDimens = TabDimenDefault(defaultTabPadding)
            }
            calculateMargins()
            return true
        }
        return false
    }

    @CallSuper
    internal open fun calculateMargins(){
        if (mRecalculateTextWidths || isInEditMode) {
            recalculateTextWidths()
            mRecalculateTextWidths = false
        }

        tabDimens!!.getSideMargin(this, containerWidth!!, getTabTextWidthsSum()).toInt().let {
            if (it != sideMargin){
                sideMargin = it
                sideMarginChanged = true
                post { requestLayout() }
            }
        }
    }


    open fun updateLayoutParams() {
        if (!sideMarginChanged) return
        sideMarginChanged = false
        layoutParams = (layoutParams as MarginLayoutParams).apply {
            marginStart = sideMargin
            marginEnd = sideMargin
        }
    }

    internal fun getTabTextWidthsSum() =
        tabTextWidthsList.reduce { acc, fl ->  acc + fl }

    open fun setCustomTabDimen(tabDimenImpl: TabDimen){
        tabDimens = tabDimenImpl
        requestLayout()
    }

    fun interface TabDimen{
        fun getSideMargin(tabLayout: TabLayout, containerWidthPixels: Int, totalTabTextsWidth: Float): Float
    }

    class TabDimenDefault(val defaultTabPadding: Float): TabDimen{
        override fun getSideMargin(tabLayout: TabLayout, containerWidthPixels: Int, totalTabTextsWidth: Float): Float {

            val res = tabLayout.resources
            val configuration = res.configuration

            val tabLayoutPaddingMin = res.getDimension(R.dimen.oui_tab_layout_default_side_margin)

            if (isLandscape(configuration) && isDisplayTypeSub(configuration)) {
                return tabLayoutPaddingMin / 2f
            }

            if (configuration.smallestScreenWidthDp <= 480) {
                return tabLayoutPaddingMin
            }

            val tabLayoutPaddingMax = containerWidthPixels * 0.125f
            val totalTabPaddings = defaultTabPadding * tabLayout.tabCount * 2

            return ((containerWidthPixels - totalTabTextsWidth - totalTabPaddings) / 2f)
                .coerceIn(tabLayoutPaddingMin, tabLayoutPaddingMax)
        }
    }

    companion object{
        private const val DEPTH_TYPE_MAIN = 1
        private const val DEPTH_TYPE_SUB = 2

        private const val TAG = "MarginsTabLayout"
    }


}

