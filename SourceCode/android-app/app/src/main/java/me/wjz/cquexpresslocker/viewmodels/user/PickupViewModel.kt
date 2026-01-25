package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.PickupExpressData
import me.wjz.cquexpresslocker.network.PickupExpressRequest
import me.wjz.cquexpresslocker.network.OpenCompartmentRequest

sealed class PickupUiState {
    data object Initial : PickupUiState()
    data object Loading : PickupUiState()
    data class Success(val data: PickupExpressData) : PickupUiState()
    data class Error(val message: String) : PickupUiState()
    data object OpeningCompartment : PickupUiState()
}

class PickupViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    
    private val expressId: String = savedStateHandle["expressId"] ?: ""
    
    private val _uiState = MutableStateFlow<PickupUiState>(PickupUiState.Initial)
    val uiState: StateFlow<PickupUiState> = _uiState
    
    fun pickup(pickupCode: String) {
        viewModelScope.launch {
            try {
                _uiState.value = PickupUiState.Loading
                val response = ApiClient.apiService.pickupExpress(
                    PickupExpressRequest(
                        expressId = expressId,
                        pickupCode = pickupCode
                    )
                )
                
                if (response.code == 200 && response.data != null) {
                    _uiState.value = PickupUiState.Success(response.data)
                } else {
                    _uiState.value = PickupUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = PickupUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun openCompartment() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                _uiState.value = PickupUiState.OpeningCompartment
                val response = ApiClient.apiService.openCompartment(
                    OpenCompartmentRequest(expressId = expressId)
                )
                
                if (response.code == 200) {
                    // 开柜成功，恢复到成功页面
                    if (currentState is PickupUiState.Success) {
                        _uiState.value = currentState
                    }
                } else {
                    _uiState.value = PickupUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = PickupUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = PickupUiState.Initial
    }
}
