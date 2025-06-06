package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Kiểm tra trạng thái đăng nhập sau 2 giây
        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser != null) {
                // Nếu đã đăng nhập, chuyển đến MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Nếu chưa đăng nhập, chuyển đến LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)

        binding.startBtn.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}