package com.android.on_track.data.geofenceDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "geofence_table")
data class GeofenceEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name_column")
    var entry_name: String = "",

    @ColumnInfo(name = "radius_column")
    var geofence_radius: Double = 10.0,

    @ColumnInfo(name = "isEnabled_column")
    var isEnabled: Boolean = true,

    @ColumnInfo(name = "location_column")
    var location: LatLng? = null
)