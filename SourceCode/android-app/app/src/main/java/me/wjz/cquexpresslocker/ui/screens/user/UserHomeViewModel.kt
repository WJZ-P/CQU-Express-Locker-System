package me.wjz.cquexpresslocker.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                // TODO: 替换为真实 API 调用
                // val response = apiService.getUserProfile()
                // val pendingExpress = apiService.getPendingExpress()
                
                // 模拟网络延迟
                delay(500)
                
                // 模拟数据
                val data = UserHomeData(
                    userName = "WJZ",
                    numOfPackages = 6,
                    pendingExpressList = listOf(
                        ExpressItem("SF1234567890", "顺丰速运", "L001-C03", "123456", "2小时前"),
                        ExpressItem("YT9876543210", "圆通速递", "L002-C05", "654321", "5小时前")
                    ),
                    recentStorage = StorageItem(
                        location = "L001-C02",
                        pickupCode = "998877",
                        status = "寄存中"
                    )
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
}

/**
 * 首页 UI 状态
 */
sealed class UserHomeUiState {
    object Loading : UserHomeUiState()
    data class Success(val data: UserHomeData) : UserHomeUiState()
    data class Error(val message: String) : UserHomeUiState()
}

/**
 * 首页数据
 */
data class UserHomeData(
    val userName: String,
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
