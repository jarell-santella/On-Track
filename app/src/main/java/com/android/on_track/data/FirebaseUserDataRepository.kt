package com.android.on_track.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FirebaseUserDataRepository(private val userData: FirebaseUserData) {
    val currentUser: Flow<User?> = userData.getCurrentUser()

    fun login(email: String, password: String) {
        CoroutineScope(IO).launch {
            userData.login(email, password)
        }
    }

    fun guestLogin() {
        CoroutineScope(IO).launch {
            userData.guestLogin()
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String, accountType: String) {
        CoroutineScope(IO).launch {
            userData.register(email, password, firstName, lastName, accountType)
        }
    }

    fun signOut() {
        CoroutineScope(IO).launch {
            userData.signOut()
        }
    }
}