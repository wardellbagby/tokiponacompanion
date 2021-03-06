package com.wardellbagby.tokipona.util

import android.os.Build
import android.support.annotation.IdRes
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.transition.AutoTransition
import android.transition.Transition
import android.view.View
import com.wardellbagby.tokipona.ui.fragment.BaseFragment

/**
 * Utility class containing useful methods for Fragments.
 * @author Wardell Bagby
 */
object Fragments {

    /**
     * Using [manager], replaces the current fragment with id [id] with [fragmentToAdd]. This will
     * correctly set the default animations and handle adding the fragment to the backstack using
     * [tag]
     */
    fun replace(manager: FragmentManager, @IdRes id: Int, fragmentToAdd: Fragment, tag: String) {
        val transaction = manager.beginTransaction().addToBackStack(tag)
        val currentFragment: Fragment? = manager.findFragmentById(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setFragmentTransitions(currentFragment, fragmentToAdd)
        }

        transaction
                .setReorderingAllowed(true)
                .replace(id, fragmentToAdd, tag)
                .commit()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setFragmentTransitions(currentFragment: Fragment?, fragmentToAdd: Fragment) {
        applyTransitionsToFragment(currentFragment)
        applyTransitionsToFragment(fragmentToAdd)
        if (currentFragment is BaseFragment) {
            excludeChildrenFromTransitions(currentFragment, currentFragment.getTargetsToExcludeFromTransitions())
            excludeChildrenFromTransitions(fragmentToAdd, currentFragment.getTargetsToExcludeFromTransitions())
        }
    }

    private fun excludeChildrenFromTransitions(fragment: Fragment, children: List<View>) {
        children.forEach {
            fragment.apply {
                excludeChildFromTransition(enterTransition, it)
                excludeChildFromTransition(exitTransition, it)
                excludeChildFromTransition(reenterTransition, it)
                excludeChildFromTransition(returnTransition, it)
            }
        }
    }

    private fun excludeChildFromTransition(transition: Any, child: View) = (transition as? Transition)?.excludeTarget(child, true)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun applyTransitionsToFragment(fragment: Fragment?) {
        fragment?.apply {
            allowEnterTransitionOverlap = false
            allowReturnTransitionOverlap = false

            reenterTransition = getDefaultEnterTransition()
            enterTransition = getDefaultEnterTransition()
            returnTransition = getDefaultExitTransition()
            exitTransition = getDefaultExitTransition()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP) private fun getDefaultEnterTransition() = AutoTransition()
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP) private fun getDefaultExitTransition() = AutoTransition()
}