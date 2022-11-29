package com.android.on_track

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng

object Util {
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    fun latLngToString(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    fun stringToLatLng(str: String): LatLng{
        val list = str.split(',')
        return LatLng(list[0].toDouble(), list[1].toDouble())
    }
}
