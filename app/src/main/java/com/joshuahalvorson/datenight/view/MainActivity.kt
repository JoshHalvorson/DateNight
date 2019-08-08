package com.joshuahalvorson.datenight.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.joshuahalvorson.datenight.BuildConfig
import com.joshuahalvorson.datenight.R
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AppBarConfiguration.OnNavigateUpListener {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupNavigation(navController)
        setupActionBar(navController, appBarConfiguration)

        AppCenter.start(
            application, BuildConfig.app_secret,
            Analytics::class.java, Crashes::class.java
        )
    }

    private fun setupActionBar(navController: NavController, appBarConfig: AppBarConfiguration) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupNavigation(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
        sideNavView?.setupWithNavController(navController)
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.randomRestaurantFragment),
            drawerLayout
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        if (navigationView == null) {

            menuInflater.inflate(R.menu.nav_menu, menu)
            return true
        }
        return retValue
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home ->
                drawer_layout.openDrawer(GravityCompat.START)
        }
        if (item != null) {
            return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
        }
        return super.onOptionsItemSelected(item)
    }
}