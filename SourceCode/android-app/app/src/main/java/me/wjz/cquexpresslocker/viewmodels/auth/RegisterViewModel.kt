package me.wjz.cquexpresslocker.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.RegisterData
import me.wjz.cquexpresslocker.network.RegisterRequest
import me.wjz.cquexpresslocker.network.SendCodeRequest

sealed class SendCodeState {
    object Idle : SendCodeState()
    object Loading : SendCodeState()
    data class Success(val message: String) : SendCodeState()
    data class Error(val message: String) : SendCodeState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val data: RegisterData) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val _sendCodeState = MutableStateFlow<SendCodeState>(SendCodeState.Idle)
    val sendCodeState: StateFlow<SendCodeState> = _sendCodeState.asStateFlow()

    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    //  注册状态流
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()

    fun sendCode(phone: String) {
        // 验证手机号
        if (phone.isEmpty()) {
            _sendCodeState.value = SendCodeState.Error("请输入手机号")
            return
        }

        if (phone.length != 11 || !phone.all { it.isDigit() }) {
            _sendCodeState.value = SendCodeState.Error("手机号格式不正确")
            return
        }

        viewModelScope.launch {
            _sendCodeState.value = SendCodeState.Loading
            try {
                val response = ApiClient.apiService.sendCode(
                    SendCodeRequest(phone = phone, type = "register")
                )

                if (response.code == 200) {
                    _sendCodeState.value = SendCodeState.Success("验证码已发送")
                    startCountdown()
                } else {
                    _sendCodeState.value = SendCodeState.Error(response.message)
                }
            } catch (e: Exception) {
                _sendCodeState.value = SendCodeState.Error(e.message ?: "网络错误")
            }
        }
    }

    //  实际注册账号逻辑
    fun register(phone: String, verifyCode: String, password: String, userType: String) {
        viewModelScope.launch {
            try {
                _registerState.value = RegisterState.Loading
                val registerRequest = RegisterRequest(phone, password, verifyCode, userType)
                val response = ApiClient.apiService.register(registerRequest)
                if (response.code == 200) {
                    _registerState.value = RegisterState.Success(response.data)
                } else {
                    _registerState.value = RegisterState.Error(response.message)
                }
            } catch (e: Exception) {
                // 网络错误 (比如超时、连不上服务器)
                e.printStackTrace()
                _registerState.value = RegisterState.Error("注册失败: ${e.message}")
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            _remainingTime.value = 60
            repeat(60) {
                delay(1000)
                _remainingTime.value = 60 - (it + 1)
            }
            _sendCodeState.value = SendCodeState.Idle
        }
    }
}
