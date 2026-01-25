package me.wjz.cquexpresslocker.viewmodels.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient.apiService
import me.wjz.cquexpresslocker.network.CourierProfileData
import me.wjz.cquexpresslocker.network.BindLockerData

sealed class CourierHomeUiState {
    object Initial : CourierHomeUiState()
    object Loading : CourierHomeUiState()
    data class Success(
        val profile: CourierProfileData,
        val lockers: List<BindLockerData>
    ) : CourierHomeUiState()
    data class Error(val message: String) : CourierHomeUiState()
}

class CourierHomeViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<CourierHomeUiState>(CourierHomeUiState.Initial)
    val uiState: StateFlow<CourierHomeUiState> = _uiState
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CourierHomeUiState.Loading
            try {
                val response = apiService.getCourierProfile()
                if (response.code == 200 && response.data != null) {
                    _uiState.value = CourierHomeUiState.Success(
                        profile = response.data,
                        lockers = response.data.bindLockers
                    )
                } else {
                    _uiState.value = CourierHomeUiState.Error(response.message ?: "获取数据失败")
                }
            } catch (e: Exception) {
                _uiState.value = CourierHomeUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun retry() {
        loadData()
    }
}
