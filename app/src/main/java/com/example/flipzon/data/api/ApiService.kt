package com.example.flipzon.data.api

import com.example.flipzon.data.api.model.AddCartRequest
import com.example.flipzon.data.api.model.AddCartResponse
import com.example.flipzon.data.api.model.LoginRequest
import com.example.flipzon.data.api.model.LoginResponse
import com.example.flipzon.data.api.model.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): Response<ProductResponse>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): Response<ProductResponse>

    @POST("carts/add")
    suspend fun checkout(@Body request: AddCartRequest): Response<AddCartResponse>
}
