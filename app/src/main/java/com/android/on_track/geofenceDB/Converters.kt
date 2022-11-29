package com.android.on_track.geofenceDB

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class Converters{
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return ""
    }

    @TypeConverter
    fun toLatLng(latLngStr: String): LatLng {
        return LatLng(0.0, 0.0)
    }
}