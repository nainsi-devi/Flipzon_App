package com.example.flipzon.data.repository

import com.example.flipzon.data.api.RetrofitClient
import com.example.flipzon.data.api.model.Product
import com.example.flipzon.data.api.model.ProductResponse

class ProductRepository {

    private val api = RetrofitClient.apiService

    suspend fun getProducts(limit: Int, skip: Int): Result<ProductResponse> {
        return try {
            val response = api.getProducts(limit, skip)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<ProductResponse> {
        return try {
            val response = api.searchProducts(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
