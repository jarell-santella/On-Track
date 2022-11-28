package com.android.on_track.data

data class User(
    val isAnonymous: Boolean,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val accountType: String? = null
)