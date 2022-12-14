package com.android.on_track.ui.loginregister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.data.FirebaseUserDataRepository

class LoginRegisterViewModelFactory(private val repository: FirebaseUserDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return LoginRegisterViewModel(repository) as T
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unknown VideoModel class")
        }
    }
}