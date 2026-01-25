package me.wjz.cquexpresslocker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.VerifyTokenRequest
import me.wjz.cquexpresslocker.utils.TokenManager

sealed class SplashUiState {
    object Loading : SplashUiState()
    data class GoToHome(val isUser: Boolean) : SplashUiState()
    object GoToLogin : SplashUiState()
}

class SplashViewModel : ViewModel() {
    private val _splashState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val splashState: StateFlow<SplashUiState> = _splashState.asStateFlow()

    fun checkTokenValidity() {
        viewModelScope.launch {
            try {
                // 从本地存储获取 token（只获取第一个值）
                val savedToken = TokenManager.getToken().firstOrNull()

                if (savedToken.isNullOrEmpty()) {
                    // 没有保存的 token，跳转到登录
                    _splashState.value = SplashUiState.GoToLogin
                    return@launch
                }

                // 有 token，验证其有效性
                val response = ApiClient.apiService.verifyToken(
                    VerifyTokenRequest(token = savedToken!!)
                )

                if (response.code == 200 && response.data?.valid == true) {
                    // Token 有效，刷新 token 并进入主页
                    val newToken = response.data.token ?: savedToken
                    val userId = response.data.userId ?: ""
                    val userType = response.data.userType ?: "user"

                    // 更新本地 token
                    TokenManager.saveToken(newToken!!, userId, userType, "")

                    _splashState.value = SplashUiState.GoToHome(isUser = userType == "user" || userType == "admin")
                } else {
                    // Token 无效或过期，跳转到登录
                    TokenManager.clearAll()
                    _splashState.value = SplashUiState.GoToLogin
                }
            } catch (e: Exception) {
                // 网络错误或其他错误，跳转到登录
                _splashState.value = SplashUiState.GoToLogin
            }
        }
    }
}
