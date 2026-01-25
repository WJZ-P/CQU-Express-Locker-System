package me.wjz.cquexpresslocker.viewmodels.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient.apiService
import me.wjz.cquexpresslocker.network.CourierDeliverRequest
import me.wjz.cquexpresslocker.network.CourierDeliverResponse
import me.wjz.cquexpresslocker.network.BindLockerData
import me.wjz.cquexpresslocker.network.ReceiverInfoData

sealed class ReceiverQueryState {
    object Idle : ReceiverQueryState()
    object Loading : ReceiverQueryState()
    data class Success(val receiver: ReceiverInfoData) : ReceiverQueryState()
    data class Error(val message: String) : ReceiverQueryState()
}

sealed class DeliverState {
    object Idle : DeliverState()
    object Loading : DeliverState()
    data class Success(val response: CourierDeliverResponse) : DeliverState()
    data class Error(val message: String) : DeliverState()
}

class CourierDeliverViewModel : ViewModel() {
    
    private val _lockers = MutableStateFlow<List<BindLockerData>>(emptyList())
    val lockers: StateFlow<List<BindLockerData>> = _lockers
    
    private val _receiverQueryState = MutableStateFlow<ReceiverQueryState>(ReceiverQueryState.Idle)
    val receiverQueryState: StateFlow<ReceiverQueryState> = _receiverQueryState
    
    private val _deliverState = MutableStateFlow<DeliverState>(DeliverState.Idle)
    val deliverState: StateFlow<DeliverState> = _deliverState
    
    init {
        loadLockers()
    }
    
    private fun loadLockers() {
        viewModelScope.launch {
            try {
                val response = apiService.getCourierProfile()
                if (response.code == 200 && response.data != null) {
                    _lockers.value = response.data.bindLockers
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun queryReceiver(phone: String) {
        viewModelScope.launch {
            _receiverQueryState.value = ReceiverQueryState.Loading
            try {
                val response = apiService.queryReceiver(phone)
                if (response.code == 200 && response.data != null) {
                    _receiverQueryState.value = ReceiverQueryState.Success(response.data)
                } else {
                    _receiverQueryState.value = ReceiverQueryState.Error(response.message ?: "查询失败")
                }
            } catch (e: Exception) {
                _receiverQueryState.value = ReceiverQueryState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun deliverExpress(
        lockerId: String,
        compartmentSize: String,
        trackingNo: String,
        receiverPhone: String,
        receiverName: String
    ) {
        viewModelScope.launch {
            _deliverState.value = DeliverState.Loading
            try {
                val request = CourierDeliverRequest(
                    lockerId = lockerId,
                    compartmentSize = compartmentSize,
                    trackingNo = trackingNo,
                    receiverPhone = receiverPhone,
                    receiverName = receiverName
                )
                val response = apiService.deliverExpress(request)
                if (response.code == 200 && response.data != null) {
                    _deliverState.value = DeliverState.Success(response.data)
                } else {
                    _deliverState.value = DeliverState.Error(response.message ?: "投递失败")
                }
            } catch (e: Exception) {
                _deliverState.value = DeliverState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    fun resetDeliverState() {
        _deliverState.value = DeliverState.Idle
    }
}
