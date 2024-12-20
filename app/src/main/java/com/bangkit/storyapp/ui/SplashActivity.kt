package com.bangkit.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.local.datastore.UserPreferences

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val token = UserPreferences(this).getToken()
            if(token != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1000)

    }
}