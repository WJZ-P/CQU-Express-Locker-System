package me.wjz.cquexpresslocker.network

import android.R.attr.data
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://localhost:6666/api/v1/"

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
    val type : Unit
)

/**
 * 注册响应数据
 */
data class RegisterData(
    val userId: String
)

interface ApiService {

    /**
     * 用户注册，或者重置密码
     */
    @POST("auth/send-code")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterData>
}

object ApiClient {

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
