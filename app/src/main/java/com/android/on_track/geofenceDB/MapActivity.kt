package com.android.on_track.geofenceDB

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.on_track.R
import com.android.on_track.Util
import com.android.on_track.databinding.ActivityMapBinding
import com.android.on_track.ui.geofence.GeofenceFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    private var isNew = true
    private var radius = 0.0
    private lateinit var name: String
    private lateinit var latLng: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.geomap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        isNew = intent.getBooleanExtra(GeofenceFragment.KEY_IS_NEW, true)
        name = intent.getStringExtra(GeofenceFragment.KEY_NAME).toString()
        radius = intent.getDoubleExtra(GeofenceFragment.KEY_RADIUS, 0.0)
        latLng = Util.stringToLatLng(intent.getStringExtra(GeofenceFragment.KEY_LAT_LNG)!!)

        Toast.makeText(this, "isNew: $isNew, name: $name, radius: $radius latLng: $latLng", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(!isNew){
            mMap.addMarker(MarkerOptions().position(latLng).title("Geofence location"))

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            mMap.animateCamera(cameraUpdate) // centers and zooms into location
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)) // centers but doesn't zoom
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun clickedCancel(view: View) {
        finish()
    }
}