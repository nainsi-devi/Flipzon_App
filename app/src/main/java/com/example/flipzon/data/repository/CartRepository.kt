package com.example.flipzon.data.repository

import com.example.flipzon.data.api.RetrofitClient
import com.example.flipzon.data.api.model.AddCartRequest
import com.example.flipzon.data.api.model.AddCartResponse
import com.example.flipzon.data.api.model.CartProduct
import com.example.flipzon.data.local.CartDao
import com.example.flipzon.data.local.CartEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    private val api = RetrofitClient.apiService

    fun getCartItems(): Flow<List<CartEntity>> = cartDao.getAllCartItems()

    suspend fun addToCart(productId: Int, title: String, price: Double, thumbnail: String) {
        val existing = cartDao.getCartItem(productId)
        if (existing != null) {
            cartDao.updateQuantity(productId, existing.quantity + 1)
        } else {
            cartDao.insertCartItem(
                CartEntity(
                    productId = productId,
                    title = title,
                    price = price,
                    thumbnail = thumbnail,
                    quantity = 1
                )
            )
        }
    }

    suspend fun increaseQuantity(productId: Int) {
        val item = cartDao.getCartItem(productId)
        if (item != null) {
            cartDao.updateQuantity(productId, item.quantity + 1)
        }
    }

    suspend fun decreaseQuantity(productId: Int) {
        val item = cartDao.getCartItem(productId)
        if (item != null) {
            if (item.quantity <= 1) {
                cartDao.deleteCartItem(productId)
            } else {
                cartDao.updateQuantity(productId, item.quantity - 1)
            }
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun checkout(userId: Int, items: List<CartEntity>): Result<AddCartResponse> {
        return try {
            val products = items.map { CartProduct(id = it.productId, quantity = it.quantity) }
            val request = AddCartRequest(userId = userId, products = products)
            val response = api.checkout(request)
            if (response.isSuccessful && response.body() != null) {
                cartDao.clearCart()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Checkout failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
