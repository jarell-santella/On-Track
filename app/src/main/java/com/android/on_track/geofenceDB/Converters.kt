package com.android.on_track.geofenceDB

import android.widget.Toast
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class Converters{
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(latLngStr: String): LatLng {
        val list = latLngStr.split(',')
        return LatLng(list[0].toDouble(), list[1].toDouble())
    }
}