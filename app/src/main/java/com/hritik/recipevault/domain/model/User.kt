package com.hritik.recipevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isPremium: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
