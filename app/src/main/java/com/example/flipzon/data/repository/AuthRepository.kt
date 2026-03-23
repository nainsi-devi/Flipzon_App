package com.example.flipzon.data.repository

import com.example.flipzon.data.api.RetrofitClient
import com.example.flipzon.data.api.model.LoginRequest
import com.example.flipzon.data.api.model.LoginResponse

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
