package com.android.on_track.ui.geofence

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.on_track.R
import com.android.on_track.Util
import com.android.on_track.createChannel
import com.android.on_track.databinding.ActivityMapBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.slider.Slider

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    private lateinit var slider: Slider

    private val gadgetQ = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private lateinit var saveButton: Button

    private var isNew = true
    private var radius = 50.0
    private lateinit var name: String
    private lateinit var latLng: LatLng
    private var newLatLng: LatLng? = null
    private var circ : Circle? = null

    private lateinit var geoClient: GeofencingClient
//    private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 3
//    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 4
//    private val REQUEST_LOCATION_PERMISSION = 10
//    private  val REQUEST_TURN_DEVICE_LOCATION_ON =20

    private val geofenceList = ArrayList<Geofence>()
//    private val geofenceIntent: PendingIntent by lazy {
//        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveButton = findViewById(R.id.btnSave)

        createChannel(this)
        geoClient = LocationServices.getGeofencingClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.geomap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        isNew = intent.getBooleanExtra(GeofenceFragment.KEY_IS_NEW, true)

        if(!isNew){
            findViewById<ConstraintLayout>(R.id.layout_new_info).visibility = View.GONE
            findViewById<LinearLayout>(R.id.buttons).setBackgroundResource(0)

            name = intent.getStringExtra(GeofenceFragment.KEY_NAME).toString()
            radius = intent.getDoubleExtra(GeofenceFragment.KEY_RADIUS, 0.0)
            latLng = Util.stringToLatLng(intent.getStringExtra(GeofenceFragment.KEY_LAT_LNG)!!)

            Toast.makeText(this, "isNew: $isNew, name: $name, radius: $radius latLng: $latLng", Toast.LENGTH_LONG).show()

            findViewById<TextView>(R.id.location_title).text = name

            geofenceList.add(Geofence.Builder()
                .setRequestId("entry.key")
                .setCircularRegion(latLng.latitude, latLng.longitude, radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build())
        }
        else{
            findViewById<Button>(R.id.btn_delete).visibility = View.GONE

            slider = findViewById<Slider>(R.id.geofence_slider)
            slider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                radius = value.toDouble()

                //TODO only do this is LatLng is not null
                if(newLatLng != null) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng!!, 17f)
                    mMap.animateCamera(cameraUpdate) // centers and zooms into location

                    if (circ != null)
                        circ!!.remove()

                    circ = mMap.addCircle(
                        CircleOptions().center(newLatLng!!)
                            .strokeColor(Color.BLUE)
                            .fillColor(0x330000FF)
                            .strokeWidth(6.0f)
                            .radius(value.toDouble())
                    )
                }
            })
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        newLatLng = latLng

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng!!, 17f)
        mMap.animateCamera(cameraUpdate) // centers and zooms into location

        if(circ != null)
            circ!!.remove()

        circ = mMap.addCircle(CircleOptions().center(newLatLng!!)
            .strokeColor(Color.BLUE)
            .fillColor(0x330000FF)
            .strokeWidth(6.0f)
            .radius(radius))

        if(!saveButton.isEnabled)
            saveButton.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
//        examinePermissionAndInitiateGeofence()
    }

//    private fun examinePermissionAndInitiateGeofence() {
//        if (authorizedLocation()) {
//            validateGadgetAreaInitiateGeofence()
//        } else {
//            askLocationPermission()
//        }
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if(!isNew){
//            mMap.addMarker(MarkerOptions().position(latLng).title("Geofence location"))

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            mMap.animateCamera(cameraUpdate) // centers and zooms into location

            circ = mMap.addCircle(CircleOptions().center(latLng)
                .strokeColor(Color.BLUE)
                .fillColor(0x330000FF)
                .strokeWidth(6.0f)
                .radius(radius)
            )
//            startLocation()
        }
        else{
            mMap.setOnMapLongClickListener(this)
            Toast.makeText(this, "Do a long click to place geofence", Toast.LENGTH_SHORT).show()
        }
    }

//    // check if background and foreground permissions are approved
//    @TargetApi(29)
//    private fun authorizedLocation(): Boolean {
//        val formalizeForeground = (
//                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_FINE_LOCATION
//                ))
//        val formalizeBackground =
//            if (gadgetQ) {
//                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                )
//            } else {
//                true
//            }
//        return formalizeForeground && formalizeBackground
//    }

//    //requesting background and foreground permissions
//    @TargetApi(29)
//    private fun askLocationPermission() {
//        if (authorizedLocation())
//            return
//        var grantingPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//        val customResult = when {
//            gadgetQ -> {
//                grantingPermission += Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
//            }
//            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
//        }
//        Log.d("DEBUG: ", "askLocationPermission: ")
//        ActivityCompat.requestPermissions(
//            this,
//            grantingPermission,
//            customResult
//        )
//    }

//    private fun startLocation() {
//        if (isPermissionGranted()) {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PERMISSION_GRANTED
//            ) {
//                return
//            }
//            mMap.isMyLocationEnabled = true
//        } else {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        }
//    }

//    private fun isPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this, Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PERMISSION_GRANTED
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.isNotEmpty() && (grantResults[0] == PERMISSION_GRANTED))
//                startLocation()
//        }
//    }

//    private fun validateGadgetAreaInitiateGeofence(resolve: Boolean = true) {
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_LOW_POWER
//        }
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//
//        val client = LocationServices.getSettingsClient(this)
//        val locationResponses =
//            client.checkLocationSettings(builder.build())
//
//        locationResponses.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException && resolve) {
//                try {
//                    exception.startResolutionForResult(
//                        this,
//                        REQUEST_TURN_DEVICE_LOCATION_ON
//                    )
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.d("DEBUG: ", "Error geting location settings resolution: " + sendEx.message)
//                }
//            } else {
//                Toast.makeText(this, "Enable your location", Toast.LENGTH_SHORT).show()
//            }
//        }
//        locationResponses.addOnCompleteListener {
//            if (it.isSuccessful) {
//                addGeofence()
//            }
//        }
//    }

//    //adding a geofence
//    private fun addGeofence(){
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
//            return
//        }
//        geoClient.addGeofences(seekGeofencing(), geofenceIntent).run {
//            addOnSuccessListener {
//                Toast.makeText(this@MapActivity, "Geofences added", Toast.LENGTH_SHORT).show()
//            }
//            addOnFailureListener {
//                Toast.makeText(this@MapActivity, "Failed to add geofences", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    //specify the geofence to monitor and the initial trigger
//    private fun seekGeofencing(): GeofencingRequest {
//        return GeofencingRequest.Builder().apply {
//            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            addGeofences(geofenceList)
//        }.build()
//    }

    override fun onDestroy() {
        super.onDestroy()
//        removeGeofence()
    }

//    //removing a geofence
//    private fun removeGeofence(){
//        geoClient.removeGeofences(geofenceIntent)?.run {
//            addOnSuccessListener {
//                Toast.makeText(this@MapActivity, "Geofences removed", Toast.LENGTH_SHORT).show()
//
//            }
//            addOnFailureListener {
//                Toast.makeText(this@MapActivity, "Failed to remove geofences", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        validateGadgetAreaInitiateGeofence(false)
//    }

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

    fun clickedSave(view: View) {
        val locNameStr = findViewById<EditText>(R.id.enter_name_field).text.toString()

        val returnIntent = Intent()
        returnIntent.putExtra(GeofenceFragment.KEY_NAME, locNameStr)
        returnIntent.putExtra(GeofenceFragment.KEY_LAT_LNG, Util.latLngToString(newLatLng!!))
        returnIntent.putExtra(GeofenceFragment.KEY_RADIUS, radius)

        setResult(1337, returnIntent)
        finish()
    }
}
