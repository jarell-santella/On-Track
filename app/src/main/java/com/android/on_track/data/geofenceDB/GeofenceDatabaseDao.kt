package com.android.on_track.data.geofenceDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeofenceEntry(geofenceEntry: GeofenceEntry)

    @Query("SELECT * FROM geofence_table")
    fun getAllGeofenceEntries(): Flow<List<GeofenceEntry>>

    @Query("DELETE FROM geofence_table")
    suspend fun deleteAllGeofenceEntries()

    @Query("DELETE FROM geofence_table WHERE id = :key")
    suspend fun deleteGeofenceEntry(key: Long)
}
