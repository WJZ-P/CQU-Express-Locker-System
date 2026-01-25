package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.ExpressItemData

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
}
