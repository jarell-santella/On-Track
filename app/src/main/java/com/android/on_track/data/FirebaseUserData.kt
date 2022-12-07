package com.android.on_track.data

import android.content.Context
import android.view.View
import android.widget.Toast
import com.android.on_track.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseUserData(private val auth: FirebaseAuth, private val db: FirebaseFirestore, private val view: View? = null, private val context: Context? = null) {
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

    fun login(email: String, password: String) {
        try {
            println("debug: test")
            auth.signInWithEmailAndPassword(email, password)
        } catch (e: FirebaseAuthInvalidUserException) {
            println("debug: test2")
            if (view != null) {
                println("debug: firebaseauthinvalduser")
                val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
                val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)

                emailInput.error = "Email or password might be wrong"
                passwordInput.error = "Email or password might be wrong"
            }
        } catch (e: Exception) {
            println("debug: test3")
            if (context != null) {
                Toast.makeText(context, "Cannot sign in", Toast.LENGTH_SHORT)
            }
        }
    }

    suspend fun guestLogin() {
        auth.signInAnonymously().await()
    }

    fun register(email: String, password: String, firstName: String, lastName: String, accountType: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                CoroutineScope(IO).launch {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // Create new document with user ID
                        // Add user object into this document
                        val user = User(currentUser.isAnonymous, firstName, lastName, currentUser.email, accountType)
                        db.collection("users").document(currentUser.uid).set(user).await()
                    }
                }
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            if (view != null) {
                val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
                emailInput.error = "Email already in use"
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            if (view != null) {
                val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
                passwordInput.error = "Password is weak"
            }
        } catch (e: Exception) {
            if (context != null) {
                Toast.makeText(context, "Cannot register", Toast.LENGTH_SHORT)
            }
        }
    }

    suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            // Delete anonymous accounts from Firebase on sign out
            auth.currentUser!!.delete().await()
        }
        auth.signOut()
    }
}