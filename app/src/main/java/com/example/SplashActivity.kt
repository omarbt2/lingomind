package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import com.example.data.SupabaseClient

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SplashScreen::class.java.getMethod("installSplashScreen", Activity::class.java).invoke(null, this)
        super.onCreate(savedInstanceState)
        SupabaseClient.init(this)

        val intent = if (SupabaseClient.isAuthenticated()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
