package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.network.ExpressItemData
import me.wjz.cquexpresslocker.network.PickupExpressRequest

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data object Loading : ScanUiState()
    data class ScannedResult(val express: ExpressItemData) : ScanUiState()
    data class PickupInProgress(val express: ExpressItemData) : ScanUiState()
    data class PickupSuccess(val expressId: String, val compartmentNo: String, val lockerName: String) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
    data object NoExpress : ScanUiState()
}

class UserScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    /**
     * 模拟扫描二维码 - 获取待取件的第一个快递
     */
    fun simulateScan() {
        viewModelScope.launch {
            try {
                _uiState.value = ScanUiState.Loading
                val response = ApiClient.apiService.getPendingExpress()

                if (response.code == 200) {
                    val items = response.data.list
                    if (items.isEmpty()) {
                        _uiState.value = ScanUiState.NoExpress
                    } else {
                        // 获取第一个快递
                        val firstExpress = items[0]
                        _uiState.value = ScanUiState.ScannedResult(firstExpress)
                    }
                } else {
                    _uiState.value = ScanUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 一键取件
     */
    fun pickupExpress(express: ExpressItemData) {
        viewModelScope.launch {
            try {
                _uiState.value = ScanUiState.PickupInProgress(express)
                val response = ApiClient.apiService.pickupExpress(
                    PickupExpressRequest(
                        expressId = express.expressId,
                        pickupCode = express.pickupCode
                    )
                )

                if (response.code == 200) {
                    _uiState.value = ScanUiState.PickupSuccess(
                        expressId = express.expressId,
                        compartmentNo = response.data.compartmentNo,
                        lockerName = response.data.lockerName
                    )
                } else {
                    _uiState.value = ScanUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "网络错误")
            }
        }
    }

    /**
     * 取消扫描，返回初始状态
     */
    fun cancel() {
        _uiState.value = ScanUiState.Idle
    }
}
