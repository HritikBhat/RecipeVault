package com.hritik.recipevault.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hritik.recipevault.data.local.datastore.UserPreferences
import com.hritik.recipevault.domain.model.User
import com.hritik.recipevault.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override val currentUser: Flow<User?> = userPreferences.userData

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User is null"))

            val user = User(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis()
            )

            // Save locally first so the user can access the app immediately
            userPreferences.saveUser(user)

            // Try to sync with Firestore but don't block login if it hangs or fails
            try {
                withTimeoutOrNull(5000) {
                    saveUserToFirestore(user)
                }
            } catch (e: Exception) {
                // Ignore sync errors during login to avoid infinite loading
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        userPreferences.clearUser()
    }

    override suspend fun saveUserToFirestore(user: User): Result<Unit> {
        return try {
            val userMap = mapOf(
                "uid" to user.uid,
                "displayName" to user.displayName,
                "email" to user.email,
                "photoUrl" to user.photoUrl,
                "isPremium" to user.isPremium,
                "createdAt" to user.createdAt
            )
            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
