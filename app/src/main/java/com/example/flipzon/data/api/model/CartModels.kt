package com.example.flipzon.data.api.model

data class AddCartRequest(
    val userId: Int,
    val products: List<CartProduct>
)

data class CartProduct(
    val id: Int,
    val quantity: Int
)

data class AddCartResponse(
    val id: Int,
    val products: List<Any>,
    val total: Double
)
