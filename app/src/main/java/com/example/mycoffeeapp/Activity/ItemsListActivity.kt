package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.ItemListCategoryAdapter
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.ViewModel.MainViewModel
import com.example.mycoffeeapp.databinding.ActivityItemsListBinding

class ItemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsListBinding
    private val viewModel = MainViewModel()
    private var id: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        initList()
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemsListActivity, Observer {
                listView.layoutManager =
                    LinearLayoutManager(
                        this@ItemsListActivity,
                        LinearLayoutManager.VERTICAL, false
                    )
                listView.adapter = ItemListCategoryAdapter(it)
                progressBar.visibility = View.GONE
            })
            backBtn.setOnClickListener { finish() }
        }
    }

    private fun getBundle() {
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

        binding.categoryTxt.text = title
    }
}