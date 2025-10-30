package com.example.ohuatl.data

import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

private const val BASE_URL = "http://10.0.2.2:8080" // emulador Android -> localhost

object ApiClient {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
}

interface ApiService {
    @POST("/auth/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("/auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("/balances/carbon")
    suspend fun getCarbonBalance(): CarbonBalance

    @GET("/history")
    suspend fun getHistory(): List<HistoryRow>

    @POST("/tokens/mint")
    suspend fun mint(@Body req: MintRequest): MintResponse
}

@Serializable
data class RegisterRequest(val email: String, val password: String, val fullName: String)
@Serializable
data class RegisterResponse(val userId: String, val email: String)
@Serializable
data class LoginRequest(val email: String, val password: String)
@Serializable
data class LoginResponse(val accessToken: String)

@Serializable
data class CarbonBalance(val totalCo2Kg: Double, val mintableTokens: Double, val minted: Double = 0.0)

@Serializable
data class HistoryRow(val day: String, val co2Kg: Double, val tokens: Double)

@Serializable
data class MintRequest(val amountTokens: Double)

@Serializable
data class MintResponse(val txHash: String, val amountTokens: Double)
