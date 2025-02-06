@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("DrawerNavigation")

package dev.oneuiproject.oneui.navigation

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import dev.oneuiproject.oneui.layout.NavDrawerLayout
import dev.oneuiproject.oneui.navigation.delegates.OnBackAppBarHandler
import dev.oneuiproject.oneui.navigation.widget.DrawerNavigationView

@JvmName("setup")
fun NavDrawerLayout.setupNavigation(
    drawerNavigationView: DrawerNavigationView,
    navHostFragment: NavHostFragment,
    configuration: AppBarConfiguration
) {
    val navController = navHostFragment.navController
    val startDestination = navController.graph.findStartDestination()

    val onBackAppBarHandler = OnBackAppBarHandler(this, navController, configuration).apply {
        this.startDestination = startDestination }

    navHostFragment.childFragmentManager.addOnBackStackChangedListener(onBackAppBarHandler)

    drawerNavigationView.setNavigationItemSelectedListener { item ->
        if (item.itemId == navController.currentDestination?.id) {
            return@setNavigationItemSelectedListener false
        }

        onNavDestinationSelected(item, navController, startDestination.id).also{ handled ->
            if (handled) {
                if (isActionMode) endActionMode()
                if (isSearchMode) endSearchMode()
                setDrawerOpen(false, animate = true, ignoreOnNavRailMode = true)
                closeNavRailOnBack = item.itemId == startDestination.id
            }else{
                navController.currentDestination?.let {
                   drawerNavigationView.updateSelectedItem(it)
                }
            }
        }
    }


    navController.addOnDestinationChangedListener(
        object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                if (destination is FloatingWindow) return
                drawerNavigationView.updateSelectedItem(destination)
                onBackAppBarHandler.onDestinationChanged(destination)
            }
        }
    )
}


private inline fun onNavDestinationSelected(item: MenuItem,
                                            navController: NavController,
                                            startDestinationId: Int): Boolean {

    val options = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setRestoreState(true)
        .setPopUpTo(startDestinationId, inclusive = false, saveState = true)
        .build()

    return try {
        navController.navigate(item.itemId, null, options)
        true
    }catch (_: IllegalArgumentException){
        false
    }
}


private inline fun NavController.getPopupDestinationLabel(): CharSequence? {
    return previousBackStackEntry?.destination?.label
}

private inline fun NavController.isPopupDestinationTop(): CharSequence? {
    return previousBackStackEntry?.destination?.label
}
