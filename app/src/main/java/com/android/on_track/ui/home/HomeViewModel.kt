package com.android.on_track.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.data.User

class HomeViewModel(private val repository: FirebaseUserDataRepository) : ViewModel() {
    val currentUser: LiveData<User?> = repository.currentUser.asLiveData()

    fun signOut() {
        repository.signOut()
    }
}