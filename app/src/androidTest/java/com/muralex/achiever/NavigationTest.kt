package com.muralex.achiever


import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest : BaseUITest() {


    @Test
    fun navigate_from_drawer() {
        navigateFromDrawer(R.id.nav_dashboard)
        navigateFromDrawer(R.id.nav_archive)
        navigateFromDrawer(R.id.nav_settings)
        navigateFromDrawer(R.id.nav_contacts)
        navigateFromDrawer(R.id.nav_home)
        waitTime(2500)
    }


    private fun navigateFromDrawer(direction: Int) {
        waitTime(500)

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open()) // Open Drawer

        waitTime(300)

        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(direction))

        waitTime(500)
    }


    private fun waitTime(time: Int) {
        try {
            Thread.sleep(time.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


}