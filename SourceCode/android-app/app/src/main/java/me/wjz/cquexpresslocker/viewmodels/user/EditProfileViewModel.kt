package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.UpdateProfileRequest

/**
 * 编辑用户资料 ViewModel
 */
sealed class EditProfileUiState {
    object Initial : EditProfileUiState()
    object Loading : EditProfileUiState()
    object Success : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

class EditProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Initial)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun updateProfile(nickname: String) {
        if (nickname.isBlank()) {
            _uiState.value = EditProfileUiState.Error("昵称不能为空")
            return
        }

        if (nickname.length > 20) {
            _uiState.value = EditProfileUiState.Error("昵称长度不能超过20个字符")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = EditProfileUiState.Loading

                val request = UpdateProfileRequest(nickname = nickname)
                val response = ApiClient.apiService.updateProfile(request)

                if (response.code == 200) {
                    _uiState.value = EditProfileUiState.Success
                } else {
                    _uiState.value = EditProfileUiState.Error(response.message ?: "更新失败")
                }
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState.Error(e.message ?: "更新失败，请稍后重试")
            }
        }
    }

    fun resetState() {
        _uiState.value = EditProfileUiState.Initial
    }
}
