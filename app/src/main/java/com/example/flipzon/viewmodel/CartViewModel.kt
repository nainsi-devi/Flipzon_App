package com.example.flipzon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.flipzon.data.local.AppDatabase
import com.example.flipzon.data.local.CartEntity
import com.example.flipzon.data.local.UserPrefs
import com.example.flipzon.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartRepo: CartRepository
    private val userPrefs: UserPrefs

    val cartItems: LiveData<List<CartEntity>>

    private val _checkoutState = MutableLiveData<CheckoutUiState>()
    val checkoutState: LiveData<CheckoutUiState> = _checkoutState

    init {
        val dao = AppDatabase.getDatabase(application).cartDao()
        cartRepo = CartRepository(dao)
        userPrefs = UserPrefs(application)
        cartItems = cartRepo.getCartItems().asLiveData()
    }

    fun addToCart(productId: Int, title: String, price: Double, thumbnail: String) {
        viewModelScope.launch {
            cartRepo.addToCart(productId, title, price, thumbnail)
        }
    }

    fun increaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepo.increaseQuantity(productId)
        }
    }

    fun decreaseQuantity(productId: Int) {
        viewModelScope.launch {
            cartRepo.decreaseQuantity(productId)
        }
    }

    fun checkout() {
        val items = cartItems.value
        if (items.isNullOrEmpty()) return

        _checkoutState.value = CheckoutUiState.Loading
        viewModelScope.launch {
            val userId = userPrefs.getUserId()
            val result = cartRepo.checkout(userId, items)
            result.onSuccess {
                _checkoutState.value = CheckoutUiState.Success
            }.onFailure {
                _checkoutState.value = CheckoutUiState.Error(it.message ?: "Checkout failed")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepo.clearCart()
        }
    }
}

sealed class CheckoutUiState {
    object Loading : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}
