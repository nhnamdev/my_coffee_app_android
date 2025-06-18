package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycoffeeapp.Adapter.ItemsAdapter
import com.example.mycoffeeapp.ViewModel.MainViewModel
import com.example.mycoffeeapp.databinding.ActivitySearchResultsBinding

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultsBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy query từ Intent
        val query = intent.getStringExtra("searchQuery") ?: ""
        if (query.isEmpty()) {
            binding.noResultsTxt.visibility = View.VISIBLE
            binding.recyclerViewSearch.visibility = View.GONE
            return
        }

        // Thiết lập back button
        binding.backButton.setOnClickListener { finish() }

        // Khởi tạo RecyclerView
        binding.recyclerViewSearch.layoutManager = GridLayoutManager(this, 2)
        initSearchResults(query)
    }

    private fun initSearchResults(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.noResultsTxt.visibility = View.GONE
        binding.recyclerViewSearch.visibility = View.GONE

        // Tìm kiếm sản phẩm
        viewModel.loadItems().observe(this) { itemsList ->
            binding.progressBar.visibility = View.GONE
            
            // Lọc sản phẩm theo query
            val filteredList = itemsList.filter { 
                it.title.lowercase().contains(query.lowercase()) 
            }

            if (filteredList.isEmpty()) {
                binding.noResultsTxt.visibility = View.VISIBLE
                binding.recyclerViewSearch.visibility = View.GONE
            } else {
                binding.noResultsTxt.visibility = View.GONE
                binding.recyclerViewSearch.visibility = View.VISIBLE
                binding.recyclerViewSearch.adapter = ItemsAdapter(filteredList)
            }
        }
    }
} 