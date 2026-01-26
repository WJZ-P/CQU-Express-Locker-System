package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.*

sealed class UserStorageUiState {
    data object Initial : UserStorageUiState()
    data object Loading : UserStorageUiState()
    data class StorageListLoaded(val data: StorageListResponse) : UserStorageUiState()
    data class CreateStorageSuccess(val data: CreateStorageResponse) : UserStorageUiState()
    data class LockerAvailabilityLoaded(val data: LockerAvailabilityResponse) : UserStorageUiState()
    data class Error(val message: String) : UserStorageUiState()
}

class UserStorageViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<UserStorageUiState>(UserStorageUiState.Initial)
    val uiState: StateFlow<UserStorageUiState> = _uiState
    
    // 快递柜可用格口信息
    private val _lockerAvailability = MutableStateFlow<LockerAvailabilityResponse?>(null)
    val lockerAvailability: StateFlow<LockerAvailabilityResponse?> = _lockerAvailability
    
    init {
        loadStorageList()
    }
    
    /**
     * 加载寄存列表
     */
    fun loadStorageList() {
        viewModelScope.launch {
            try {
                _uiState.value = UserStorageUiState.Loading
                val response = ApiClient.apiService.getStorageList()
                
                if (response.code == 200 && response.data != null) {
                    _uiState.value = UserStorageUiState.StorageListLoaded(response.data)
                } else {
                    _uiState.value = UserStorageUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = UserStorageUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    /**
     * 加载快递柜可用格口信息
     */
    fun loadLockerAvailability(lockerId: String) {
        viewModelScope.launch {
            try {
                // 提取lockerId中的数字部分
                val numericId = lockerId.filter { it.isDigit() }
                if (numericId.isEmpty()) {
                    _uiState.value = UserStorageUiState.Error("快递柜ID格式错误")
                    return@launch
                }
                
                val response = ApiClient.apiService.getLockerAvailability(numericId)
                
                if (response.code == 200 && response.data != null) {
                    _lockerAvailability.value = response.data
                    _uiState.value = UserStorageUiState.LockerAvailabilityLoaded(response.data)
                } else {
                    _uiState.value = UserStorageUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = UserStorageUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    /**
     * 创建寄存订单
     */
    fun createStorage(
        lockerId: String,
        compartmentSize: String,
        duration: Int,
        itemDescription: String = ""
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = UserStorageUiState.Loading
                val response = ApiClient.apiService.createStorage(
                    CreateStorageRequest(
                        lockerId = lockerId,
                        compartmentSize = compartmentSize,
                        duration = duration,
                        itemDescription = itemDescription
                    )
                )
                
                if (response.code == 200 && response.data != null) {
                    _uiState.value = UserStorageUiState.CreateStorageSuccess(response.data)
                    // 重新加载列表以刷新数据
                    loadStorageList()
                } else {
                    _uiState.value = UserStorageUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = UserStorageUiState.Error(e.message ?: "网络错误")
            }
        }
    }
    
    /**
     * 重置状态，用于对话框关闭后恢复
     */
    fun resetState() {
        _uiState.value = UserStorageUiState.Initial
        loadStorageList()
    }
}
