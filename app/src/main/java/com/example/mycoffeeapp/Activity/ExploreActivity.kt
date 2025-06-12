package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.databinding.ActivityExploreBinding

class ExploreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExploreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button functionality
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Directions button to navigate to MapActivity
        binding.directionsBtn.setOnClickListener {
            val address = "My Coffee Shop, 123 Coffee Street, HCM City, Vietnam"
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Fallback to browser if Google Maps is not installed
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}"))
                startActivity(browserIntent)
            }
        }

        // Call button to open dialer with a hardcoded phone number
        binding.callBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:1234567890") // Hardcoded placeholder; replace with actual number
            startActivity(intent)
        }
    }
}