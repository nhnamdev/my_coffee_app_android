package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var selectedLocation: LatLng? = null
    private var selectedAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)

        // Lấy SupportMapFragment bằng ID
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Nút quay lại
        binding.backButton.setOnClickListener {
            finish()
        }

        // Nút xác nhận
        binding.confirmButton.setOnClickListener {
            if (selectedLocation != null && selectedAddress != null) {
                val intent = intent.apply {
                    putExtra("address", selectedAddress)
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Vui lòng chọn một địa điểm", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val defaultLocation = LatLng(10.8974236, 106.7845859)  // Vị trí mặc định
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Xử lý sự kiện khi người dùng nhấn vào bản đồ
        map.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Vị trí đã chọn"))
            
            // Lấy địa chỉ từ tọa độ
            val geocoder = android.location.Geocoder(this)
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    selectedAddress = address.getAddressLine(0)
                    binding.selectedAddressText.text = selectedAddress
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Không thể lấy địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
