package com.example.flipzon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipzon.data.api.model.Product
import com.example.flipzon.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productsState = MutableLiveData<ProductsUiState>()
    val productsState: LiveData<ProductsUiState> = _productsState

    private val allProducts = mutableListOf<Product>()
    private var currentSkip = 0
    private val pageSize = 20
    private var totalProducts = 0
    private var isLoading = false
    private var searchJob: Job? = null
    private var isSearching = false

    init {
        loadProducts()
    }

    fun loadProducts() {
        if (isLoading) return
        isLoading = true
        isSearching = false

        if (allProducts.isEmpty()) {
            _productsState.value = ProductsUiState.Loading
        }

        viewModelScope.launch {
            val result = repository.getProducts(pageSize, currentSkip)
            result.onSuccess { response ->
                allProducts.addAll(response.products)
                totalProducts = response.total
                currentSkip += pageSize
                _productsState.value = if (allProducts.isEmpty()) {
                    ProductsUiState.Empty
                } else {
                    ProductsUiState.Success(allProducts.toList(), canLoadMore())
                }
                isLoading = false
            }.onFailure {
                if (allProducts.isEmpty()) {
                    _productsState.value = ProductsUiState.Error(it.message ?: "Something went wrong")
                }
                isLoading = false
            }
        }
    }

    fun loadMore() {
        if (isSearching || !canLoadMore()) return
        loadProducts()
    }

    fun searchProducts(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            isSearching = false
            if (allProducts.isNotEmpty()) {
                _productsState.value = ProductsUiState.Success(allProducts.toList(), canLoadMore())
            } else {
                resetAndLoad()
            }
            return
        }

        isSearching = true
        searchJob = viewModelScope.launch {
            delay(500)
            _productsState.value = ProductsUiState.Loading
            val result = repository.searchProducts(query)
            result.onSuccess { response ->
                _productsState.value = if (response.products.isEmpty()) {
                    ProductsUiState.Empty
                } else {
                    ProductsUiState.Success(response.products, false)
                }
            }.onFailure {
                _productsState.value = ProductsUiState.Error(it.message ?: "Search failed")
            }
        }
    }

    fun retry() {
        allProducts.clear()
        currentSkip = 0
        loadProducts()
    }

    private fun resetAndLoad() {
        allProducts.clear()
        currentSkip = 0
        loadProducts()
    }

    private fun canLoadMore(): Boolean = currentSkip < totalProducts
}

sealed class ProductsUiState {
    object Loading : ProductsUiState()
    data class Success(val products: List<Product>, val canLoadMore: Boolean) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
    object Empty : ProductsUiState()
}
