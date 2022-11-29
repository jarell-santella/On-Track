package com.android.on_track.ui.geofence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeofenceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is geofence Fragment"
    }
    val text: LiveData<String> = _text
}