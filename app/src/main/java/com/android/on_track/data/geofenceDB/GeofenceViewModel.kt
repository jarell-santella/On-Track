package com.android.on_track.data.geofenceDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class GeofenceViewModel(private val repository: GeofenceRepository) : ViewModel() {
    val allGeofenceEntriesLiveData: LiveData<List<GeofenceEntry>> = repository.allGeofenceEntries.asLiveData()

    fun insert(geofenceEntry: GeofenceEntry) {
        repository.insert(geofenceEntry)
    }

    fun deleteFirst(){
        val geofenceEntriesList = allGeofenceEntriesLiveData.value
        if (geofenceEntriesList != null && geofenceEntriesList.isNotEmpty()){
            val id = geofenceEntriesList[0].id
            repository.delete(id)
        }
    }

    fun deletePosition(pos: Int){
        val geofenceEntriesList = allGeofenceEntriesLiveData.value
        if (geofenceEntriesList != null && geofenceEntriesList.isNotEmpty()){
            val id = geofenceEntriesList[pos].id
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val geofenceEntriesList = allGeofenceEntriesLiveData.value
        if (geofenceEntriesList != null && geofenceEntriesList.isNotEmpty())
            repository.deleteAll()
    }
}

class GeofenceViewModelFactory (private val repository: GeofenceRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(GeofenceViewModel::class.java))
            return GeofenceViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}