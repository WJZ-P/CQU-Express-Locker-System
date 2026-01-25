package me.wjz.cquexpresslocker.viewmodels.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient.apiService
import me.wjz.cquexpresslocker.network.CourierProfileData

sealed class CourierProfileUiState {
    object Initial : CourierProfileUiState()
    object Loading : CourierProfileUiState()
    data class Success(val profile: CourierProfileData) : CourierProfileUiState()
    data class Error(val message: String) : CourierProfileUiState()
}

class CourierProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<CourierProfileUiState>(CourierProfileUiState.Initial)
    val uiState: StateFlow<CourierProfileUiState> = _uiState
    
    init {
        loadProfile()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = CourierProfileUiState.Loading
            try {
                val response = apiService.getCourierProfile()
                if (response.code == 200 && response.data != null) {
                    _uiState.value = CourierProfileUiState.Success(response.data)
                } else {
                    _uiState.value = CourierProfileUiState.Error(response.message ?: "获取快递员信息失败")
                }
            } catch (e: Exception) {
                _uiState.value = CourierProfileUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun retry() {
        loadProfile()
    }
}
