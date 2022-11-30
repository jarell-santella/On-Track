package com.android.on_track.ui.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.on_track.R
import com.android.on_track.ui.geofence.GeofenceFragment
import com.android.on_track.ui.navigation.dashboard.DashboardFragment
import com.android.on_track.ui.navigation.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val homeFragment = HomeFragment()
        val dashboardFragment = DashboardFragment()
        val geofenceFragment = GeofenceFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnClickListener {
            when (it.id) {
                R.id.navigation_home -> // TODO: Needs code
                R.id.navigation_dashboard -> // TODO: Needs code
                R.id.navigation_geofence -> // TODO: Needs code
            }
        }
    }
}