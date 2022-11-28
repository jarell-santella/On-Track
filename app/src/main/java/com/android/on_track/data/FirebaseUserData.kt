package com.android.on_track.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseUserData(private val auth: FirebaseAuth, private val db: FirebaseFirestore) {
    fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            auth.currentUser.let {
                if (it != null && !it.isAnonymous) {
                    // Get full User object from the database
                    val docRef = db.collection("users").document(it.uid)
                    docRef.get().addOnSuccessListener { documentSnapshot ->
                        val user = documentSnapshot.toObject<User>()
                        // Make User object
                        trySend(user)
                    }
                } else if (it != null && it.isAnonymous) {
                    // Make User object that is anonymous
                    trySend(User(it.isAnonymous))
                } else {
                    // No current user (signed out)
                    trySend(null)
                }
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun guestLogin() {
        auth.signInAnonymously().await()
    }

    suspend fun register(email: String, password: String, firstName: String, lastName: String, accountType: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            CoroutineScope(IO).launch {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Create new document with user ID
                    // WARNING: .await() here may cause issues
                    db.collection("users").add(currentUser.uid).await()

                    // Add user object into this document
                    // WARNING: .await() here may cause issues
                    val user = User(currentUser.isAnonymous, firstName, lastName, currentUser.email, accountType)
                    db.collection("users").document(currentUser.uid).set(user).await()
                }
            }
        }
    }

    suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            // Delete anonymous accounts from Firebase on sign out
            // CAUTION: All information on these anonymous accounts will be lost if signed out from
            // WARNING: .await() here may cause issues
            auth.currentUser!!.delete().await()
        }
        auth.signOut()
    }
}