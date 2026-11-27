package com.musicapp.data.remote

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutations
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rx.preferenceDataStoreFactory
import androidx.datastore.rx.RxDataStore
import com.google.common.util.concurrent.ListenableFuture
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val context: Context,
    private val dataStore: RxDataStore<Preferences>
) {
    private val ACCESS_TOKEN_KEY = preferencesKey<String>("access_token")
    private val REFRESH_TOKEN_KEY = preferencesKey<String>("refresh_token")
    private val TOKEN_EXPIRY_KEY = preferencesKey<Long>("token_expiry")

    fun getAccessToken(): String {
        // For synchronous access, we'd need to handle this differently
        // In practice, this would be a suspend function or use a cached value
        return getTokenSync()
    }

    private fun getTokenSync(): String {
        // Simplified - in real app use runBlocking or cache
        return ""
    }

    suspend fun setTokens(accessToken: String, refreshToken: String, expiresIn: Long) {
        val expiry = System.currentTimeMillis() + expiresIn
        dataStore.updateDataAsync { preferences ->
            preferences.mutations()
                .set(ACCESS_TOKEN_KEY, accessToken)
                .set(REFRESH_TOKEN_KEY, refreshToken)
                .set(TOKEN_EXPIRY_KEY, expiry)
        }.await()
    }

    suspend fun clearTokens() {
        dataStore.updateDataAsync { preferences ->
            preferences.mutations()
                .remove(ACCESS_TOKEN_KEY)
                .remove(REFRESH_TOKEN_KEY)
                .remove(TOKEN_EXPIRY_KEY)
        }.await()
    }

    suspend fun isTokenExpired(): Boolean {
        val expiry = dataStore.data
            .map { it[TOKEN_EXPIRY_KEY] ?: 0L }
            .firstOrNull() ?: 0L
        return System.currentTimeMillis() >= expiry
    }

    suspend fun refreshToken(): Result<String> {
        // Implement token refresh logic
        // Call auth endpoint with refresh token
        // Update tokens on success
        return Result.success("")
    }
}

interface TokenManagerInterface {
    fun getAccessToken(): String
    suspend fun setTokens(accessToken: String, refreshToken: String, expiresIn: Long)
    suspend fun clearTokens()
    suspend fun isTokenExpired(): Boolean
    suspend fun refreshToken(): Result<String>
}