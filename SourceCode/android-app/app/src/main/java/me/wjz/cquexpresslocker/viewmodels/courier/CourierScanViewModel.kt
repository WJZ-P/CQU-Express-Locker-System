package me.wjz.cquexpresslocker.viewmodels.courier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.BindLockerData
import me.wjz.cquexpresslocker.network.CourierDeliverRequest
import me.wjz.cquexpresslocker.network.LockerAvailabilityResponse
import me.wjz.cquexpresslocker.network.ReceiverInfoData

sealed class CourierScanState {
    data object Idle : CourierScanState()
    data object ScanningLocker : CourierScanState()
    data class LockerScanned(val locker: BindLockerData) : CourierScanState()
    data object SelectingCompartment : CourierScanState()
    data class CompartmentSelected(
        val locker: BindLockerData,
        val compartmentSize: String
    ) : CourierScanState()
    data object Delivering : CourierScanState()
    data class DeliverySuccess(val compartmentNo: String, val pickupCode: String) : CourierScanState()
    data class Error(val message: String) : CourierScanState()
}

sealed class ReceiverQueryState {
    data object Idle : ReceiverQueryState()
    data object Loading : ReceiverQueryState()
    data class Success(val receiver: ReceiverInfoData) : ReceiverQueryState()
    data class Error(val message: String) : ReceiverQueryState()
}

class CourierScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CourierScanState>(CourierScanState.Idle)
    val uiState: StateFlow<CourierScanState> = _uiState

    private val _lockers = MutableStateFlow<List<BindLockerData>>(emptyList())
    val lockers: StateFlow<List<BindLockerData>> = _lockers

    private val _availabilityData = MutableStateFlow<LockerAvailabilityResponse?>(null)
    val availabilityData: StateFlow<LockerAvailabilityResponse?> = _availabilityData

    private val _receiverQueryState = MutableStateFlow<ReceiverQueryState>(ReceiverQueryState.Idle)
    val receiverQueryState: StateFlow<ReceiverQueryState> = _receiverQueryState

    init {
        loadCourierLockers()
    }

    /**
     * 加载快递员绑定的快递柜
     */
    private fun loadCourierLockers() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCourierProfile()
                if (response.code == 200) {
                    _lockers.value = response.data.bindLockers
                } else {
                    _uiState.value = CourierScanState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = CourierScanState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 模拟扫描柜门二维码 - 随机选择一个绑定的快递柜
     */
    fun simulateScanLocker() {
        viewModelScope.launch {
            try {
                _uiState.value = CourierScanState.ScanningLocker
                
                if (_lockers.value.isEmpty()) {
                    _uiState.value = CourierScanState.Error("没有绑定快递柜")
                    return@launch
                }
                
                // 随机选择一个快递柜
                val randomLocker = _lockers.value.random()
                _uiState.value = CourierScanState.LockerScanned(randomLocker)
                
                // 查询快递柜可用格口信息
                loadLockerAvailability(randomLocker.lockerId)
            } catch (e: Exception) {
                _uiState.value = CourierScanState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 查询快递柜可用格口信息
     */
    private fun loadLockerAvailability(lockerId: String) {
        viewModelScope.launch {
            try {
                // 提取lockerId中的数字部分
                val numericId = lockerId.filter { it.isDigit() }
                if (numericId.isEmpty()) {
                    _uiState.value = CourierScanState.Error("快递柜ID格式错误")
                    return@launch
                }
                
                val response = ApiClient.apiService.getLockerAvailability(numericId)
                if (response.code == 200) {
                    _availabilityData.value = response.data
                } else {
                    _uiState.value = CourierScanState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = CourierScanState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 选择格口大小
     */
    fun selectCompartmentSize(size: String, locker: BindLockerData) {
        _uiState.value = CourierScanState.CompartmentSelected(locker, size)
    }

    /**
     * 查询收件人信息
     */
    fun queryReceiver(phone: String) {
        viewModelScope.launch {
            _receiverQueryState.value = ReceiverQueryState.Loading
            try {
                val response = ApiClient.apiService.queryReceiver(phone)
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

    /**
     * 进行投递
     */
    fun deliverExpress(locker: BindLockerData, compartmentSize: String, trackingNo: String, receiverPhone: String, receiverName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = CourierScanState.Delivering
                
                // 提取lockerId中的数字部分
                val numericLockerId = locker.lockerId.filter { it.isDigit() }
                if (numericLockerId.isEmpty()) {
                    _uiState.value = CourierScanState.Error("快递柜ID格式错误")
                    return@launch
                }
                
                val request = CourierDeliverRequest(
                    lockerId = numericLockerId,
                    compartmentSize = compartmentSize,
                    trackingNo = trackingNo,
                    receiverPhone = receiverPhone,
                    receiverName = receiverName
                )
                
                val response = ApiClient.apiService.deliverExpress(request)
                
                if (response.code == 200) {
                    _uiState.value = CourierScanState.DeliverySuccess(
                        compartmentNo = response.data.compartmentNo,
                        pickupCode = response.data.pickupCode
                    )
                } else {
                    _uiState.value = CourierScanState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = CourierScanState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 重置状态
     */
    fun reset() {
        _uiState.value = CourierScanState.Idle
        _availabilityData.value = null
        _receiverQueryState.value = ReceiverQueryState.Idle
    }

    /**
     * 返回上一步
     */
    fun goBack() {
        when (_uiState.value) {
            is CourierScanState.CompartmentSelected -> {
                _uiState.value = CourierScanState.SelectingCompartment
            }
            is CourierScanState.SelectingCompartment -> {
                val currentLocker = (_uiState.value as? CourierScanState.LockerScanned)?.locker
                if (currentLocker != null) {
                    _uiState.value = CourierScanState.LockerScanned(currentLocker)
                } else {
                    _uiState.value = CourierScanState.Idle
                }
            }
            is CourierScanState.LockerScanned -> {
                _uiState.value = CourierScanState.Idle
            }
            else -> {
                _uiState.value = CourierScanState.Idle
            }
        }
    }
}
