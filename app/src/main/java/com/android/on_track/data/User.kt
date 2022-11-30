package com.android.on_track.data

data class User(
    @field:JvmField
    val isAnonymous: Boolean? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val accountType: String? = null
)