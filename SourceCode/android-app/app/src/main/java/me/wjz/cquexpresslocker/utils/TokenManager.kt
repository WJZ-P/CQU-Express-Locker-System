package me.wjz.cquexpresslocker.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_TYPE_KEY = stringPreferencesKey("user_type")
    private val NICKNAME_KEY = stringPreferencesKey("nickname")
    
    private lateinit var dataStore: DataStore<Preferences>
    
    fun initialize(context: Context) {
        dataStore = context.dataStore
    }
    
    // 保存 token 及用户信息
    suspend fun saveToken(token: String, userId: String, userType: String, nickname: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_TYPE_KEY] = userType
            preferences[NICKNAME_KEY] = nickname
        }
    }
    
    // 获取 token
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
    
    // 获取 user ID
    fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }
    
    // 获取 user type
    fun getUserType(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_TYPE_KEY]
        }
    }
    
    // 获取昵称
    fun getNickname(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[NICKNAME_KEY]
        }
    }
    
    // 清除所有数据（登出）
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // 检查是否有有效 token
    suspend fun hasValidToken(): Boolean {
        val prefs = dataStore.data.map { it[TOKEN_KEY] }.let { flow ->
            var token: String? = null
            flow.collect { token = it }
            token
        }
        return !prefs.isNullOrEmpty()
    }
}
