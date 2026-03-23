package com.example.flipzon.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.flipzon.data.local.UserPrefs
import com.example.flipzon.databinding.ActivityLoginBinding
import com.example.flipzon.viewmodel.AuthViewModel
import com.example.flipzon.viewmodel.LoginUiState

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var userPrefs: UserPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPrefs = UserPrefs(this)

        if (userPrefs.isLoggedIn()) {
            goToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeLogin()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    private fun observeLogin() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is LoginUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val user = state.data
                    userPrefs.saveUser(
                        id = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        image = user.image
                    )
                    goToMain()
                }
                is LoginUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
