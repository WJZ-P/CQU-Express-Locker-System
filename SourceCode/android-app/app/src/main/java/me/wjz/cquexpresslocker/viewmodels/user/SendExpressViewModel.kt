package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.SendExpressData
import me.wjz.cquexpresslocker.network.SendExpressRequest

sealed class SendExpressUiState {
    data object Initial : SendExpressUiState()
    data object Loading : SendExpressUiState()
    data class Success(val data: SendExpressData) : SendExpressUiState()
    data class Error(val message: String) : SendExpressUiState()
}

class SendExpressViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<SendExpressUiState>(SendExpressUiState.Initial)
    val uiState: StateFlow<SendExpressUiState> = _uiState
    
    fun sendExpress(
        company: String,
        senderName: String,
        senderPhone: String,
        senderAddress: String,
        receiverName: String,
        receiverPhone: String,
        receiverAddress: String,
        itemType: String = "",
        weight: String = "",
        remark: String = ""
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = SendExpressUiState.Loading
                val response = ApiClient.apiService.sendExpress(
                    SendExpressRequest(
                        company = company,
                        senderName = senderName,
                        senderPhone = senderPhone,
                        senderAddress = senderAddress,
                        receiverName = receiverName,
                        receiverPhone = receiverPhone,
                        receiverAddress = receiverAddress,
                        itemType = itemType,
                        weight = weight,
                        remark = remark
                    )
                )
                
                if (response.code == 200 && response.data != null) {
                    _uiState.value = SendExpressUiState.Success(response.data)
                } else {
                    _uiState.value = SendExpressUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = SendExpressUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = SendExpressUiState.Initial
    }
}
