package com.android.on_track.geofence

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

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