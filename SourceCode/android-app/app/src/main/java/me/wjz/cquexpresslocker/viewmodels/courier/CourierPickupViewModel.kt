package me.wjz.cquexpresslocker.viewmodels.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient.apiService
import me.wjz.cquexpresslocker.network.CollectItemData
import me.wjz.cquexpresslocker.network.OpenCompartmentRequest
import me.wjz.cquexpresslocker.network.ReturnItemData

sealed class CourierPickupUiState {
    object Initial : CourierPickupUiState()
    object Loading : CourierPickupUiState()
    data class Success(
        val collectItems: List<CollectItemData>,
        val returnItems: List<ReturnItemData>
    ) : CourierPickupUiState()
    data class Error(val message: String) : CourierPickupUiState()
}

class CourierPickupViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<CourierPickupUiState>(CourierPickupUiState.Initial)
    val uiState: StateFlow<CourierPickupUiState> = _uiState
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CourierPickupUiState.Loading
            try {
                val collectResponse = apiService.getPendingCollectList()
                val returnResponse = apiService.getPendingReturnList()
                
                if (collectResponse.code == 200 && returnResponse.code == 200
                    && collectResponse.data != null && returnResponse.data != null) {
                    _uiState.value = CourierPickupUiState.Success(
                        collectItems = collectResponse.data.list,
                        returnItems = returnResponse.data.list
                    )
                } else {
                    _uiState.value = CourierPickupUiState.Error("获取数据失败")
                }
            } catch (e: Exception) {
                _uiState.value = CourierPickupUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun retry() {
        loadData()
    }
    
    fun openCompartment(expressId: String) {
        viewModelScope.launch {
            try {
                apiService.openCompartmentCourier(
                    OpenCompartmentRequest(expressId = expressId)
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
