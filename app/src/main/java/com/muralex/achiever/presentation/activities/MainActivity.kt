package com.muralex.achiever.presentation.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.muralex.achiever.R
import com.muralex.achiever.databinding.ActivityMainBinding
import com.muralex.achiever.presentation.fragments.contacts.ContactActions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var contactActions: ContactActions

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout

        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_dashboard,
            R.id.nav_archive,
            R.id.nav_settings,
            R.id.nav_contacts
        ), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.apply {
            setupWithNavController(navController)
            setNavigationItemSelectedListener(this@MainActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        var selectItem = true

        when (item.itemId) {
            R.id.nav_share -> {
                contactActions.shareApp()
                selectItem = false
            }

            R.id.nav_home ->
                navController.popBackStack(R.id.nav_home, false)

            R.id.nav_dashboard -> {
                navController.popBackStack(R.id.nav_home, false)
                navController.navigate(R.id.nav_dashboard)
            }

            R.id.nav_archive-> {
                navController.popBackStack(R.id.nav_home, false)
                navController.navigate(R.id.nav_archive)
            }

            else -> {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

        lifecycleScope.launch {
            delay(60)
            binding.drawerLayout.closeDrawers()
        }

        return selectItem
    }


}