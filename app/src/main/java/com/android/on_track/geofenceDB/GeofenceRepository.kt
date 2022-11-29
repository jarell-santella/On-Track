package com.android.on_track.geofenceDB

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GeofenceRepository(private val geofenceDatabaseDao: GeofenceDatabaseDao) {
    val allGeofenceEntries: Flow<List<GeofenceEntry>> = geofenceDatabaseDao.getAllGeofenceEntries()

    fun insert(geofenceEntry: GeofenceEntry){
        CoroutineScope(Dispatchers.IO).launch{
            geofenceDatabaseDao.insertGeofenceEntry(geofenceEntry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            geofenceDatabaseDao.deleteGeofenceEntry(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            geofenceDatabaseDao.deleteAllGeofenceEntries()
        }
    }
}