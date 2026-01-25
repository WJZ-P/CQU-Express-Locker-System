package me.wjz.cquexpresslocker.network

import com.google.gson.GsonBuilder
import me.wjz.cquexpresslocker.BuildConfig
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
    val userType: String
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
}

object ApiClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val gson = GsonBuilder().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
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
