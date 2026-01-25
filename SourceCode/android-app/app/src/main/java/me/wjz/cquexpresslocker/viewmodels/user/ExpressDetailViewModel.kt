package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.ExpressDetail

sealed class ExpressDetailUiState {
    data object Loading : ExpressDetailUiState()
    data class Success(val detail: ExpressDetail) : ExpressDetailUiState()
    data class Error(val message: String) : ExpressDetailUiState()
}

class ExpressDetailViewModel(val expressId: String) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ExpressDetailUiState>(ExpressDetailUiState.Loading)
    val uiState: StateFlow<ExpressDetailUiState> = _uiState
    
    init {
        loadExpressDetail()
    }
    
    private fun loadExpressDetail() {
        viewModelScope.launch {
            try {
                _uiState.value = ExpressDetailUiState.Loading
                val response = ApiClient.apiService.getExpressDetail(expressId)
                
                if (response.code == 200 && response.data != null) {
                    _uiState.value = ExpressDetailUiState.Success(response.data)
                } else {
                    _uiState.value = ExpressDetailUiState.Error(response.message ?: "获取快递详情失败")
                }
            } catch (e: Exception) {
                _uiState.value = ExpressDetailUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun refresh() {
        loadExpressDetail()
    }
}
