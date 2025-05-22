package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycoffeeapp.Adapter.ItemsAdapter
import com.example.mycoffeeapp.ViewModel.MainViewModel
import com.example.mycoffeeapp.databinding.ActivityItemsListBinding

class ItemsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemsListBinding
    private val viewModel = MainViewModel()
    private var categoryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get categoryId from intent
        categoryId = intent.getStringExtra("categoryId") ?: ""

        initItemsList()
        setVariable()
    }

    private fun setVariable() {
        binding.backButtonLayout.setOnClickListener { finish() }
    }

    private fun initItemsList() {
        binding.progressBar.visibility = View.VISIBLE

        // Load items by category
        viewModel.loadItemCategory(categoryId).observe(this) { itemsList ->
            binding.recyclerViewItems.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewItems.adapter = ItemsAdapter(itemsList)
            binding.progressBar.visibility = View.GONE
        }
    }
}