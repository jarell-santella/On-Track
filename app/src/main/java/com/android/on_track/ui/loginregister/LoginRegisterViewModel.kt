package com.android.on_track.ui.loginregister

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.data.User
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginRegisterViewModel(private val repository: FirebaseUserDataRepository) : ViewModel() {
    val currentUser: LiveData<User?> = repository.currentUser.asLiveData()

    fun login(email: String, password: String) {
        repository.login(email, password)
    }

    fun guestLogin() {
        repository.guestLogin()
    }

    fun register(email: String, password: String, firstName: String, lastName: String, accountType: String) {
        repository.register(email, password, firstName, lastName, accountType)
    }
}