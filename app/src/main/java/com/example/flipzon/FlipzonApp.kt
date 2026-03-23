package com.example.flipzon

import android.app.Application
import com.example.flipzon.data.local.AppDatabase

class FlipzonApp : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
    }
}
