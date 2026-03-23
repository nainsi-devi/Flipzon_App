package com.example.flipzon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flipzon.data.api.model.LoginResponse
import com.example.flipzon.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<LoginUiState>()
    val loginState: LiveData<LoginUiState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = repository.login(username, password)
            result.onSuccess {
                _loginState.value = LoginUiState.Success(it)
            }.onFailure {
                _loginState.value = LoginUiState.Error(it.message ?: "Something went wrong")
            }
        }
    }
}

sealed class LoginUiState {
    object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
