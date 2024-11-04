package dev.oneuiproject.oneui.ktx

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.SectionIndexer
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SeslSwipeListAnimator
import androidx.recyclerview.widget.SeslSwipeListAnimator.SwipeConfiguration
import dev.oneuiproject.oneui.delegates.SwipeActionListener
import dev.oneuiproject.oneui.delegates.SwipeItemCallbackDelegate

/**
 * Registers a long-press multi-selection listener.
 *
 * @param onItemSelected Lambda to be invoked when an item is selected that includes the following parameters:
 * `position` - the layout position of the selected item.
 * `id`- the stable ID of the selected item. Returns [RecyclerView.NO_ID] when a stable ID is not implemented.
 *
 * @param onStateChanged Lambda to be invoked when the selection state changes that includes the following parameters:
 * `state` - either [MultiSelectionState.STARTED] or [MultiSelectionState.ENDED].
 *`position` - the layout position of the selected item when [MultiSelectionState.STARTED].
 * This is always set to [RecyclerView.NO_POSITION] when [MultiSelectionState.ENDED].
 *
 */
inline fun RecyclerView.doOnLongPressMultiSelection (
    crossinline onItemSelected: (position: Int,
                                 id: Long) -> Unit,
    crossinline onStateChanged: (state: MultiSelectionState,
                                 position: Int) -> Unit = { _, _ -> },
){
    seslSetLongPressMultiSelectionListener(
        object : RecyclerView.SeslLongPressMultiSelectionListener {
            override fun onItemSelected(
                view: RecyclerView,
                child: View,
                position: Int,
                id: Long
            ) {
                onItemSelected.invoke(position, id)
            }

            override fun onLongPressMultiSelectionStarted(x: Int, y: Int) {
                val child = findChildViewUnder(x.toFloat(), y.toFloat())
                onStateChanged.invoke(
                    MultiSelectionState.STARTED,
                    child?.let { getChildLayoutPosition(it) } ?: NO_POSITION
                )
            }
            override fun onLongPressMultiSelectionEnded(x: Int, y: Int) {
                onStateChanged.invoke(
                    MultiSelectionState.ENDED,
                    NO_POSITION
                )
            }
        })
}

enum class MultiSelectionState{
    STARTED,
    ENDED
}


/**
 * Syntactic sugar equivalent to calling:
 *
 * ```
 * RecyclerView.apply{
 *     seslSetFillBottomEnabled(true)
 *     seslSetLastRoundedCorner(true)
 *     seslSetFastScrollerEnabled(true)
 *     seslSetGoToTopEnabled(true)
 *     seslSetSmoothScrollEnabled(true)
 *     seslSetIndexTipEnabled(true)
 * }
 * ```
 */
inline fun RecyclerView.enableCoreSeslFeatures(
    fillBottom:Boolean = true,
    lastRoundedCorner:Boolean = true,
    fastScrollerEnabled:Boolean = true,
    goToTopEnabled:Boolean = true,
    smoothScrollEnabled:Boolean = true,
    indexTipEnabled: Boolean = adapter is SectionIndexer
){
    if (fillBottom) seslSetFillBottomEnabled(true)
    if (lastRoundedCorner) seslSetLastRoundedCorner(true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && fastScrollerEnabled) {
        seslSetFastScrollerEnabled(true)
    }
    if (goToTopEnabled) seslSetGoToTopEnabled(true)
    if (smoothScrollEnabled) seslSetSmoothScrollEnabled(true)
    if (indexTipEnabled) seslSetIndexTipEnabled(true)
}

/**
 * Configures swipe animation functionality for a [RecyclerView] using Samsung's SESL Swipe List Animator.
 *
 * @param leftToRightLabel Text label to display when swiping items from left to right. Default is an empty string.
 * @param rightToLeftLabel Text label to display when swiping items from right to left. Default is an empty string.
 * @param leftToRightColor Color of the swipe indicator for left-to-right swipe. Default is unset (-1).
 * @param rightToLeftColor Color of the swipe indicator for right-to-left swipe. Default is unset (-1).
 * @param leftToRightDrawableRes Drawable resource ID for the swipe indicator icon when swiping from left to right. Default is null.
 * @param rightToLeftDrawableRes Drawable resource ID for the swipe indicator icon when swiping from right to left. Default is null.
 * @param drawablePadding Padding between the swipe indicator icon and the swipe label text. Default is 20dp converted to pixels.
 * @param textColor Color of the swipe label text. Default is unset (-1).
 * @param onSwiped Lambda function to be invoked when an item is swiped. Return true to dismiss the item, false to cancel. Receives position, direction, and action state.
 * @param isLeftSwipeEnabled Lambda function to determine if left swipe is enabled for a specific view holder. Default always returns true.
 * @param isRightSwipeEnabled Lambda function to determine if right swipe is enabled for a specific view holder. Default always returns true.
 */
inline fun RecyclerView.configureItemSwipeAnimator(
    leftToRightLabel: String = "",
    rightToLeftLabel: String = "",
    @ColorInt
    leftToRightColor: Int = -1,//UNSET_VALUE
    @ColorInt
    rightToLeftColor: Int = -1,
    @DrawableRes
    leftToRightDrawableRes: Int? = null,
    @DrawableRes
    rightToLeftDrawableRes: Int? = null,
    @Px
    drawablePadding: Int = 20.dpToPx(resources),
    @ColorInt
    textColor: Int = -1,
    crossinline onSwiped: (position: Int, direction: Int, actionState: Int) -> Boolean,
    crossinline onCleared: () -> Unit = {},
    crossinline isLeftSwipeEnabled: (viewHolder: ViewHolder) -> Boolean = { true },
    crossinline isRightSwipeEnabled:(viewHolder: ViewHolder) -> Boolean = { true }
) {
    val context = context

    val swipeConfiguration = SwipeConfiguration().apply sc@{
        textLeftToRight = leftToRightLabel
        textRightToLeft = rightToLeftLabel
        this@sc.drawablePadding = drawablePadding
        this@sc.textColor = textColor
        colorLeftToRight = leftToRightColor
        colorRightToLeft = rightToLeftColor
        drawableLeftToRight = leftToRightDrawableRes?.let {
            AppCompatResources.getDrawable(context, it)?.apply {
                setTint(Color.parseColor("#CCFAFAFF"))
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }
        drawableRightToLeft = rightToLeftDrawableRes?.let {
            AppCompatResources.getDrawable(context, it)?.apply {
                setTint(Color.parseColor("#CCFAFAFF"))
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }
    }

    val seslSwipeListAnimator = SeslSwipeListAnimator(this, context).apply {
        setSwipeConfiguration(swipeConfiguration)
    }

    val swipeActionListener = object : SwipeActionListener {
        override fun isLeftSwipeEnabled(viewHolder: ViewHolder): Boolean {
            return isLeftSwipeEnabled.invoke(viewHolder)
        }

        override fun isRightSwipeEnabled(viewHolder: ViewHolder): Boolean {
            return isRightSwipeEnabled.invoke(viewHolder)
        }

        override fun onCleared() {
            onCleared()
        }

        override fun onSwiped(
            position: Int,
            swipeDirection: Int,
            actionState: Int
        ): Boolean {
            return onSwiped.invoke(position, swipeDirection, actionState)
        }

    }

    ItemTouchHelper(
        SwipeItemCallbackDelegate(
            seslSwipeListAnimator,
            swipeActionListener
        )
    ).attachToRecyclerView(this)
}

