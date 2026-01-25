package me.wjz.cquexpresslocker.network

import com.google.gson.GsonBuilder
import me.wjz.cquexpresslocker.BuildConfig
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import me.wjz.cquexpresslocker.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * 通用响应结构
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

// ===================== 认证模块 =====================

/**
 * 登录请求
 */
data class LoginRequest(
    val phone: String,
    val password: String
)

/**
 * 登录响应数据
 */
data class LoginData(
    val token: String,
    val userId: String,
    val userType: String,
    val nickname: String
)

/**
 * 发送验证码请求
 */
data class SendCodeRequest(
    val phone: String,
    val type: String
)

/**
 * 注册请求
 */
data class RegisterRequest(
    val phone: String,
    val password: String,
    val verifyCode: String,
    val userType: String,
    val username: String
)

/**
 * 注册响应数据
 */
data class RegisterData(
    val userId: String
)

/**
 * Token 校验请求
 */
data class VerifyTokenRequest(
    val token: String
)

/**
 * Token 校验响应数据
 */
data class VerifyTokenData(
    val valid: Boolean,
    val token: String? = null,
    val expiresIn: Long? = null,
    val userId: String? = null,
    val userType: String? = null
)

// ===================== 用户模块 =====================

/**
 * 绑定快递柜信息
 */
data class BindLocker(
    val lockerId: String,
    val lockerName: String,
    val address: String
)

/**
 * 用户信息
 */
data class UserProfile(
    val userId: String,
    val phone: String,
    val nickname: String,
    val bindLockers: List<BindLocker>
)

/**
 * 更新用户信息请求
 */
data class UpdateProfileRequest(
    val nickname: String
)

// ===================== 快递模块 =====================

/**
 * 快递项信息
 */
data class ExpressItemData(
    val expressId: String,
    val trackingNo: String,
    val company: String,
    val lockerName: String,
    val compartmentNo: String,
    val status: String,
    val arrivalTime: String,
    val pickupCode: String,
    val deadline: String
)

/**
 * 待取快递列表响应
 */
data class PendingExpressListData(
    val total: Int,
    val list: List<ExpressItemData>
)

/**
 * 快递详情
 */
data class ExpressDetail(
    val expressId: String,
    val trackingNo: String,
    val company: String,
    val lockerName: String,
    val lockerAddress: String,
    val compartmentNo: String,
    val compartmentSize: String,
    val status: String,
    val arrivalTime: String,
    val pickupCode: String,
    val deadline: String,
    val senderName: String,
    val senderPhone: String,
    val receiverName: String,
    val receiverPhone: String
)

/**
 * 取件请求
 */
data class PickupExpressRequest(
    val expressId: String,
    val pickupCode: String
)

/**
 * 取件响应
 */
data class PickupExpressData(
    val compartmentNo: String,
    val lockerName: String
)

/**
 * 开柜请求
 */
data class OpenCompartmentRequest(
    val expressId: String
)

/**
 * 寄件请求
 */
data class SendExpressRequest(
    val company: String,
    val senderName: String,
    val senderPhone: String,
    val senderAddress: String,
    val receiverName: String,
    val receiverPhone: String,
    val receiverAddress: String,
    val itemType: String,
    val weight: String,
    val remark: String
)

/**
 * 寄件响应
 */
data class SendExpressData(
    val orderId: String,
    val estimatedPickupTime: String,
    val estimatedFee: Double
)

/**
 * 分页通用结构
 */
data class PageData<T>(
    val records: List<T>,
    val total: Long? = null,
    val size: Long? = null,
    val current: Long? = null,
    val pages: Long? = null
)

/**
 * 寄存记录（旧版接口）
 */
data class StorageOrderItem(
    val id: Long? = null,
    val orderNo: String? = null,
    val type: Int? = null,
    val trackingNo: String? = null,
    val pickupCode: String? = null,
    val boxId: Long? = null,
    val courierId: Long? = null,
    val userId: Long? = null,
    val receiverPhone: String? = null,
    val status: Int? = null,
    val createTime: String? = null,
    val finishTime: String? = null
)

// ===================== 寄存模块（新版） =====================

/**
 * 创建寄存请求
 */
data class CreateStorageRequest(
    val lockerId: String,           // 快递柜ID
    val compartmentSize: String,    // small/medium/large
    val duration: Int,              // 寄存时长(小时)
    val itemDescription: String     // 物品描述
)

/**
 * 创建寄存响应
 */
data class CreateStorageResponse(
    val storageId: String,          // 寄存单号
    val compartmentNo: String,      // 格口号
    val openCode: String,           // 6位取件码
    val expireTime: String,         // 过期时间
    val fee: Double                 // 费用
)

/**
 * 寄存列表项
 */
data class StorageListItem(
    val storageId: String,
    val lockerName: String,
    val compartmentNo: String,
    val compartmentSize: String,
    val status: String,            // active/completed/expired
    val createTime: String,
    val expireTime: String,
    val openCode: String,
    val itemDescription: String
)

/**
 * 寄存列表响应
 */
data class StorageListResponse(
    val total: Int,
    val list: List<StorageListItem>
)

/**
 * 快递柜可用格口信息
 */
data class LockerAvailabilityResponse(
    val lockerId: Long,
    val lockerName: String,
    val smallCount: Int,
    val mediumCount: Int,
    val largeCount: Int
)

// ===================== 历史记录模块 =====================

/**
 * 历史记录项
 */
data class HistoryItemData(
    val recordId: String,
    val type: String,              // pickup/send/storage
    val title: String,
    val lockerName: String,
    val compartmentNo: String,
    val time: String,
    val status: String,            // pending/completed/expired
    val company: String,
    val trackingNo: String
)

/**
 * 历史记录列表响应
 */
data class HistoryListResponse(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val list: List<HistoryItemData>
)

interface ApiService {

    // ===================== 认证接口 =====================

    /**
     * 用户登录
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    /**
     * 发送验证码
     */
    @POST("auth/send-code")
    suspend fun sendCode(@Body request: SendCodeRequest): ApiResponse<Unit>

    /**
     * 用户注册
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterData>

    /**
     * Token 校验与刷新
     */
    @POST("auth/verify-token")
    suspend fun verifyToken(@Body request: VerifyTokenRequest): ApiResponse<VerifyTokenData>

    // ===================== 用户接口 =====================

    /**
     * 获取用户信息
     */
    @GET("user/profile")
    suspend fun getUserProfile(): ApiResponse<UserProfile>

    /**
     * 更新用户信息
     */
    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ApiResponse<Unit>

    // ===================== 快递接口 =====================

    /**
     * 获取待取快递列表
     */
    @GET("express/pending")
    suspend fun getPendingExpress(): ApiResponse<PendingExpressListData>

    /**
     * 获取快递详情
     */
    @GET("express/{expressId}")
    suspend fun getExpressDetail(@Path("expressId") expressId: String): ApiResponse<ExpressDetail>

    /**
     * 取件（验证取件码）
     */
    @POST("express/pickup")
    suspend fun pickupExpress(@Body request: PickupExpressRequest): ApiResponse<PickupExpressData>

    /**
     * 开柜（已验证后再次开柜）
     */
    @POST("express/open")
    suspend fun openCompartment(@Body request: OpenCompartmentRequest): ApiResponse<Unit>

    /**
     * 寄件
     */
    @POST("express/send")
    suspend fun sendExpress(@Body request: SendExpressRequest): ApiResponse<SendExpressData>

    // ===================== 寄存接口 =====================

    /**
     * 创建寄存订单
     */
    @POST("storage/create")
    suspend fun createStorage(@Body request: CreateStorageRequest): ApiResponse<CreateStorageResponse>

    /**
     * 获取寄存列表
     */
    @GET("storage/list")
    suspend fun getStorageList(): ApiResponse<StorageListResponse>

    /**
     * 获取快递柜可用格口信息
     */
    @GET("locker/availability/{lockerId}")
    suspend fun getLockerAvailability(@Path("lockerId") lockerId: String): ApiResponse<LockerAvailabilityResponse>

    // ===================== 历史记录接口 =====================

    /**
     * 获取历史记录列表
     */
    @GET("history")
    suspend fun getHistory(
        @Query("type") type: String = "all",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<HistoryListResponse>

    // ===================== 旧版寄存接口 =====================

    /**
     * 获取寄存记录（旧版接口，非 /api/v1 前缀）
     */
    @GET("/api/storage/list")
    suspend fun getStorageListLegacy(
        @Query("current") current: Int = 1,
        @Query("size") size: Int = 1
    ): ApiResponse<PageData<StorageOrderItem>>
}


object ApiClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val gson = GsonBuilder().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val token = runBlocking { TokenManager.getToken().firstOrNull() }
            val newRequest = if (!token.isNullOrBlank()) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(newRequest)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
