package me.wjz.cquexpresslocker.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wjz.cquexpresslocker.network.ApiClient
import me.wjz.cquexpresslocker.ui.screens.user.ExpressItem
import me.wjz.cquexpresslocker.utils.TokenManager

/**
 * UserHomeScreen 的 ViewModel
 */
class UserHomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UserHomeUiState>(UserHomeUiState.Loading)
    val uiState: StateFlow<UserHomeUiState> = _uiState.asStateFlow()

    init {
        loadUserHomeData()
    }

    /**
     * 加载首页数据
     */
    fun loadUserHomeData() {
        viewModelScope.launch {
            _uiState.value = UserHomeUiState.Loading
            try {
                val profileResponse = ApiClient.apiService.getUserProfile()
                if (profileResponse.code != 200) {
                    if (isSessionInvalid(profileResponse.code, profileResponse.message)) {
                        handleSessionExpired()
                        return@launch
                    }
                    throw RuntimeException(profileResponse.message)
                }

                val pendingResponse = ApiClient.apiService.getPendingExpress()
                if (pendingResponse.code != 200) {
                    if (isSessionInvalid(pendingResponse.code, pendingResponse.message)) {
                        handleSessionExpired()
                        return@launch
                    }
                    throw RuntimeException(pendingResponse.message)
                }

                val storageResponse = ApiClient.apiService.getStorageList()
                if (storageResponse.code != 200 && isSessionInvalid(storageResponse.code, storageResponse.message)) {
                    handleSessionExpired()
                    return@launch
                }

                val userName = profileResponse.data.nickname.ifBlank { profileResponse.data.phone }
                val pendingList = pendingResponse.data.list.map { item ->
                    ExpressItem(
                        trackingNo = item.trackingNo,
                        company = item.company,
                        location = "${item.lockerName}-${item.compartmentNo}",
                        pickupCode = item.pickupCode,
                        time = item.arrivalTime
                    )
                }

                val recentStorage = if (storageResponse.code == 200) {
                    storageResponse.data.records.firstOrNull()?.let { record ->
                        StorageItem(
                            location = record.boxId?.let { "柜格$it" } ?: "-",
                            pickupCode = record.pickupCode ?: "-",
                            status = mapStorageStatus(record.status)
                        )
                    }
                } else {
                    null
                }

                val data = UserHomeData(
                    userName = userName,
                    phoneNumber = profileResponse.data.phone,
                    numOfPackages = pendingResponse.data.total,
                    pendingExpressList = pendingList,
                    recentStorage = recentStorage
                )

                _uiState.value = UserHomeUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UserHomeUiState.Error(e.message ?: "未知错误")
            }
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadUserHomeData()
    }

    private fun mapStorageStatus(status: Int?): String {
        return when (status) {
            0 -> "寄存中"
            1 -> "已取出"
            2 -> "已超时"
            else -> "未知"
        }
    }

    private suspend fun handleSessionExpired() {
        TokenManager.clearAll()
        _uiState.value = UserHomeUiState.Unauthorized
    }

    private fun isSessionInvalid(code: Int, message: String): Boolean {
        return code == 401 || message.contains("token", ignoreCase = true) || message.contains("用户不存在")
    }
}



/**
 * 首页 UI 状态
 */
sealed class UserHomeUiState {
    object Loading : UserHomeUiState()
    data class Success(val data: UserHomeData) : UserHomeUiState()
    data class Error(val message: String) : UserHomeUiState()
    object Unauthorized : UserHomeUiState()
}

/**
 * 首页数据
 */
data class UserHomeData(
    val userName: String,
    val phoneNumber: String,
    val numOfPackages: Int,
    val pendingExpressList: List<ExpressItem>,
    val recentStorage: StorageItem?
)

/**
 * 寄存物品
 */
data class StorageItem(
    val location: String,
    val pickupCode: String,
    val status: String
)
