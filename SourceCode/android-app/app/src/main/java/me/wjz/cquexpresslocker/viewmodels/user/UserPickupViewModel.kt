package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.ExpressItemData
import me.wjz.cquexpresslocker.network.OpenCompartmentRequest

sealed class UserPickupUiState {
    data object Loading : UserPickupUiState()
    data class Success(val items: List<ExpressItemData>) : UserPickupUiState()
    data class Error(val message: String) : UserPickupUiState()
    data object Empty : UserPickupUiState()
}

class UserPickupViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<UserPickupUiState>(UserPickupUiState.Loading)
    val uiState: StateFlow<UserPickupUiState> = _uiState
    
    init {
        loadPendingExpress()
    }
    
    private fun loadPendingExpress() {
        viewModelScope.launch {
            try {
                _uiState.value = UserPickupUiState.Loading
                val response = ApiClient.apiService.getPendingExpress()
                
                if (response.code == 200) {
                    val items = response.data.list
                    if (items.isEmpty()) {
                        _uiState.value = UserPickupUiState.Empty
                    } else {
                        _uiState.value = UserPickupUiState.Success(items)
                    }
                } else {
                    _uiState.value = UserPickupUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = UserPickupUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun refresh() {
        loadPendingExpress()
    }
    
    /**
     * 移除已取件的快递（局部更新，不需要重新加载整个列表）
     */
    fun removeExpressAfterPickup(expressId: String) {
        val currentState = _uiState.value
        if (currentState is UserPickupUiState.Success) {
            val updatedItems = currentState.items.filter { it.expressId != expressId }
            _uiState.value = if (updatedItems.isEmpty()) {
                UserPickupUiState.Empty
            } else {
                UserPickupUiState.Success(updatedItems)
            }
        }
    }
    
    fun openCompartment(expressId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.openCompartment(
                    OpenCompartmentRequest(expressId = expressId)
                )
                if (response.code != 200) {
                    // 开柜失败，可以在此处处理错误提示
                }
            } catch (e: Exception) {
                // 网络错误处理
            }
        }
    }
}
