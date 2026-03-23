package com.example.flipzon.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.flipzon.R
import com.example.flipzon.data.local.AppDatabase
import com.example.flipzon.data.local.UserPrefs
import com.example.flipzon.databinding.ActivityMainBinding
import com.example.flipzon.ui.fragments.CartFragment
import com.example.flipzon.ui.fragments.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPrefs: UserPrefs

    private val homeFragment = HomeFragment()
    private val cartFragment = CartFragment()
    private var activeFragmentTag = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefs(this)

        setupTopBar()
        setupBottomNav()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment, "home")
                .add(R.id.fragmentContainer, cartFragment, "cart")
                .hide(cartFragment)
                .commit()
        }
    }

    private fun setupTopBar() {
        binding.tvUserName.text = userPrefs.getFullName()
        binding.tvUserEmail.text = userPrefs.getEmail()

        val imageUrl = userPrefs.getImage()
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivProfile)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    switchFragment("home")
                    true
                }
                R.id.cartFragment -> {
                    switchFragment("cart")
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(tag: String) {
        if (tag == activeFragmentTag) return
        val transaction = supportFragmentManager.beginTransaction()
        val current = supportFragmentManager.findFragmentByTag(activeFragmentTag)
        val next = supportFragmentManager.findFragmentByTag(tag)
        if (current != null) transaction.hide(current)
        if (next != null) transaction.show(next)
        transaction.commit()
        activeFragmentTag = tag
    }

    private fun logout() {
        userPrefs.clear()
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@MainActivity).cartDao().clearCart()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
