package com.android.on_track.geofenceDB

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.on_track.R
import com.android.on_track.Util
import com.android.on_track.databinding.ActivityMapBinding
import com.android.on_track.ui.geofence.GeofenceFragment
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var locationRequest: LocationRequest

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

        if(!isNew)
            findViewById<TextView>(R.id.location_title).text = name

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(!isNew){
//            mMap.addMarker(MarkerOptions().position(latLng).title("Geofence location"))

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            mMap.animateCamera(cameraUpdate) // centers and zooms into location
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)) // centers but doesn't zoom

            mMap.addCircle(CircleOptions().center(latLng)
                .strokeColor(Color.BLUE)
                .fillColor(0x330000FF)
                .strokeWidth(6.0f)
                .radius(radius)
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun clickedCancel(view: View) {
        finish()
    }

    fun deleteEntry(view: View) {
        val returnIntent = Intent()
        returnIntent.putExtra(GeofenceFragment.KEY_LIST_INDEX, intent.getIntExtra(GeofenceFragment.KEY_LIST_INDEX, -1))
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}
