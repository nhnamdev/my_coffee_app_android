package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.R

class ActivityChecking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checking)

        // Chuyển sang PaymentSuccessActivity sau 3 giây
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
} 