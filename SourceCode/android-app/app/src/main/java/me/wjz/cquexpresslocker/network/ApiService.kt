package me.wjz.cquexpresslocker.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * 通用响应结构
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
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

interface ApiService {

    /**
     * 用户注册
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterData>
}

object ApiClient {
    private const val BASE_URL = "http://localhost:6666/api/v1/"

    private val gson = GsonBuilder().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
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
