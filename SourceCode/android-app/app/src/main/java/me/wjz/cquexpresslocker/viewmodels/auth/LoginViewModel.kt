package me.wjz.cquexpresslocker.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.LoginRequest
import me.wjz.cquexpresslocker.utils.TokenManager

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val isUser: Boolean, val token: String, val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(phone: String, password: String, userType: String) {
        // 输入验证
        if (phone.isEmpty()) {
            _loginState.value = LoginUiState.Error("请输入手机号")
            return
        }

        if (password.isEmpty()) {
            _loginState.value = LoginUiState.Error("请输入密码")
            return
        }

        if (phone.length != 11 || !phone.all { it.isDigit() }) {
            _loginState.value = LoginUiState.Error("手机号格式不正确")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response = ApiClient.apiService.login(
                    LoginRequest(phone = phone, password = password)
                )

                if (response.code == 200 && response.data != null) {
                    // 登录成功，保存 token
                    TokenManager.saveToken(
                        token = response.data.token,
                        userId = response.data.userId,
                        userType = response.data.userType,
                        nickname = response.data.nickname
                    )
                    
                    val isUser = userType == "user"
                    _loginState.value = LoginUiState.Success(
                        isUser = isUser,
                        token = response.data.token,
                        userId = response.data.userId
                    )
                } else {
                    _loginState.value = LoginUiState.Error(response.message ?: "登录失败")
                }
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(e.message ?: "网络错误")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }
}
