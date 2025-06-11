package com.example.mycoffeeapp.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.OrderAdapter
import com.example.mycoffeeapp.Helper.ManagmentOrder
import com.example.mycoffeeapp.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var managmentOrder: ManagmentOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentOrder = ManagmentOrder(this)
        setupOrderList()
        setupClickListeners()
    }

    private fun setupOrderList() {
        binding.orderView.layoutManager = LinearLayoutManager(this)
        binding.orderView.adapter = OrderAdapter(managmentOrder.getListOrder(), this)
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
} 