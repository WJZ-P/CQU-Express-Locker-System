package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.HistoryItemData

sealed class HistoryUiState {
    data object Initial : HistoryUiState()
    data object Loading : HistoryUiState()
    data class Success(
        val items: List<HistoryItemData>,
        val total: Int,
        val currentPage: Int,
        val pageSize: Int
    ) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
    data object Empty : HistoryUiState()
}

class HistoryViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Initial)
    val uiState: StateFlow<HistoryUiState> = _uiState
    
    private val _selectedType = MutableStateFlow("all")
    val selectedType: StateFlow<String> = _selectedType
    
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage
    
    init {
        loadHistory()
    }
    
    fun loadHistory(type: String = "all", page: Int = 1) {
        viewModelScope.launch {
            try {
                _uiState.value = HistoryUiState.Loading
                _selectedType.value = type
                _currentPage.value = page
                
                val response = ApiClient.apiService.getHistory(
                    type = type,
                    page = page,
                    pageSize = 20
                )
                
                if (response.code == 200) {
                    val items = response.data.list
                    if (items.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        _uiState.value = HistoryUiState.Success(
                            items = items,
                            total = response.data.total,
                            currentPage = response.data.page,
                            pageSize = response.data.pageSize
                        )
                    }
                } else {
                    _uiState.value = HistoryUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun changeTab(type: String) {
        loadHistory(type = type, page = 1)
    }
    
    fun loadNextPage() {
        if (_uiState.value is HistoryUiState.Success) {
            val current = _currentPage.value
            loadHistory(type = _selectedType.value, page = current + 1)
        }
    }
    
    fun loadPreviousPage() {
        if (_currentPage.value > 1) {
            val current = _currentPage.value
            loadHistory(type = _selectedType.value, page = current - 1)
        }
    }
    
    fun refresh() {
        loadHistory(type = _selectedType.value, page = _currentPage.value)
    }
}
