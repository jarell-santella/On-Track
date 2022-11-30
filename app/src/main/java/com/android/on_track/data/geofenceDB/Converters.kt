package com.android.on_track.data.geofenceDB

import androidx.room.TypeConverter
import com.android.on_track.Util
import com.google.android.gms.maps.model.LatLng

class Converters{
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return Util.latLngToString(latLng)
    }

    @TypeConverter
    fun toLatLng(latLngStr: String): LatLng {
        return Util.stringToLatLng(latLngStr)
    }
}