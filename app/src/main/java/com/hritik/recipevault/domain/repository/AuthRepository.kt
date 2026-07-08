package com.hritik.recipevault.domain.repository

import com.hritik.recipevault.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
    suspend fun saveUserToFirestore(user: User): Result<Unit>
}
