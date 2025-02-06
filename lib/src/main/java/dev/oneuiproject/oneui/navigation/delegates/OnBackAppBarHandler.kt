package dev.oneuiproject.oneui.navigation.delegates

import android.os.Build
import androidx.activity.BackEventCompat
import androidx.annotation.RestrictTo
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import dev.oneuiproject.oneui.design.R
import dev.oneuiproject.oneui.layout.NavDrawerLayout

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class OnBackAppBarHandler(
    private val navDrawerLayout: NavDrawerLayout,
    private val navController: NavController,
    private val configuration: AppBarConfiguration,
) : FragmentManager.OnBackStackChangedListener {

    private var isBackFragmentLabelSet = false
    //https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back#interpolation
    private val progressInterpolator = PathInterpolatorCompat.create(0.1f, 0.1f, 0f, 1f)

    private var updateTitleOnBackProgress = false
    private var backFragmentLabel: CharSequence? = null
    private var backFragmentSubtitle: CharSequence? = null
    private var backFragmentIsTop = false
    private var _showNavButtonAsBack = true

    private val context = navDrawerLayout.context
    private var navArgSubTitle: String = context.getString(R.string.navArg_subtitle)
    private var navArgExpandable: String = context.getString(R.string.navArg_expandable)
    private var navArgImmersiveScroll: String = context.getString(R.string.navArg_immersiveScroll)

    @JvmField
    var startDestination: NavDestination? = null

    override fun onBackStackChanged() {
        isBackFragmentLabelSet = false
    }

    override fun onBackStackChangeProgressed(backEventCompat: BackEventCompat) {
        if (updateTitleOnBackProgress) {
            val interpolatedProgress =
                progressInterpolator.getInterpolation(backEventCompat.progress)
            if (isBackFragmentLabelSet != interpolatedProgress > PREDICTIVE_BACK_TOOLBAR_TITLE_THRESHOLD) {
                isBackFragmentLabelSet = !isBackFragmentLabelSet
                if (isBackFragmentLabelSet) {
                    navDrawerLayout.apply {
                        showNavigationButtonAsBack = !backFragmentIsTop
                        setTitlesNoCache(
                            backFragmentLabel,
                            backFragmentLabel,
                            backFragmentSubtitle,
                            backFragmentSubtitle
                        )
                    }

                } else {
                    navDrawerLayout.apply {
                        showNavigationButtonAsBack = _showNavButtonAsBack
                        applyCachedTitles()
                    }
                }
            }
        }
    }

    override fun onBackStackChangeCancelled() {
        if (isBackFragmentLabelSet){
            navDrawerLayout.applyCachedTitles()
            isBackFragmentLabelSet = false
        }
        navDrawerLayout.showNavigationButtonAsBack = _showNavButtonAsBack
    }

    override fun onBackStackChangeCommitted(fragment: Fragment, pop: Boolean) {
        isBackFragmentLabelSet = false
    }

    fun onDestinationChanged(
        destination: NavDestination
    ) {
        val isChildDestination = startDestination?.id != destination.id
        val popupDestination = navController.previousBackStackEntry?.destination

        updateTitleOnBackProgress = isChildDestination
        if (isChildDestination && popupDestination != null) {
            backFragmentLabel = popupDestination.label
            backFragmentSubtitle = popupDestination.arguments[navArgSubTitle]?.defaultValue as? String
        } else {
            backFragmentLabel = null
            backFragmentSubtitle = null
        }

        backFragmentIsTop = popupDestination?.run { configuration.isTopLevelDestination(this) } ?: true

        navDrawerLayout.apply {
            setTitle(destination.label)
            setSubtitle(destination.arguments[navArgSubTitle]?.defaultValue as? String)

            isExpandable = destination.arguments[navArgExpandable]?.defaultValue as? Boolean ?: true
            if (Build.VERSION.SDK_INT >= 30) {
                isImmersiveScroll = destination.arguments[navArgImmersiveScroll]?.defaultValue as? Boolean ?: false
            }

            configuration.isTopLevelDestination(destination).let {
                showNavigationButtonAsBack = !it
                _showNavButtonAsBack = !it
                closeNavRailOnBack = it
            }
        }
    }

    companion object{
        private const val PREDICTIVE_BACK_TOOLBAR_TITLE_THRESHOLD = 0.3f
            private const val TAG = "OnBackAppBarHandler"
    }
}



