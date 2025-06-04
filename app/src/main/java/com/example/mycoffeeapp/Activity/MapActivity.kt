package com.example.mycoffeeapp.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy SupportMapFragment bằng ID (không dùng binding)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Nút quay lại về MainActivity
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val traSuaPU = LatLng(10.8974236, 106.7845859)  // Tọa độ của Trà Sữa PU
        map.addMarker(MarkerOptions().position(traSuaPU).title("Trà Sữa PU"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(traSuaPU, 17f))  // Bạn có thể thay đổi mức zoom
    }
}
