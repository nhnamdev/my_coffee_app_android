package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Adapter.CategoryAdapter
import com.example.mycoffeeapp.Adapter.PopularAdapter
import com.example.mycoffeeapp.Domain.CategoryModel
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Helper.ManagmentFavorite
import com.example.mycoffeeapp.ViewModel.MainViewModel
import com.example.mycoffeeapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import android.text.Editable
import android.view.inputmethod.EditorInfo

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var managmentFavorite: ManagmentFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MyCoffeeApp", MODE_PRIVATE)
        checkFirstOpenOfDay()
        initBanner()
        initCategory()
        initPopular()
        setupClickListeners()
        setupSearch()
    }

    private fun checkFirstOpenOfDay() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastOpenDate = sharedPreferences.getString("last_open_date", "")
        if (lastOpenDate != currentDate) {
            showWelcomeDialog()
            sharedPreferences.edit().putString("last_open_date", currentDate).apply()
        }
    }

    private fun showWelcomeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Chào mừng")
            .setMessage("Chào mừng bạn đến với ứng dụng My Coffee App!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanner()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.loadCategory().observeForever { categoryList ->
            val categoryModelList = categoryList.map { category ->
                CategoryModel(
                    title = category.title,
                    id = category.id
                )
            }.toMutableList()

            // Thêm danh mục Popular vào đầu danh sách
            categoryModelList.add(0, CategoryModel(title = "Popular", id = -1))

            binding.recyclerViewCat.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewCat.adapter = CategoryAdapter(categoryModelList) { selectedCategory ->
                // Khi chọn danh mục, lọc sản phẩm theo danh mục đó
                android.util.Log.d("MainActivity", "Selected category: ${selectedCategory.title} with id: ${selectedCategory.id}")
                
                // Cập nhật tiêu đề danh mục
                binding.categoryTitleTxt.text = selectedCategory.title
                
                if (selectedCategory.title == "Popular") {
                    // Nếu chọn Popular, hiển thị danh sách sản phẩm phổ biến
                    viewModel.loadPopular().observe(this@MainActivity) { popularList ->
                        val itemsModelList = popularList.map { popular ->
                            ItemsModel(
                                title = popular.title,
                                description = popular.description,
                                picUrl = ArrayList(popular.picUrl),
                                price = popular.price,
                                rating = popular.rating,
                                extra = popular.extra
                            )
                        }.toMutableList()

                        binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
                        binding.recyclerViewPopular.adapter = PopularAdapter(itemsModelList)
                    }
                } else {
                    // Xử lý các danh mục khác như bình thường
                    viewModel.loadItemCategory(selectedCategory.id.toString()).observe(this@MainActivity) { itemsList ->
                        android.util.Log.d("MainActivity", "Received ${itemsList.size} items for category ${selectedCategory.id}")
                        val itemsModelList = itemsList.map { item ->
                            ItemsModel(
                                title = item.title,
                                description = item.description,
                                picUrl = ArrayList(item.picUrl),
                                price = item.price,
                                rating = item.rating,
                                extra = item.extra,
                                categoryId = item.categoryId
                            )
                        }.toMutableList()

                        binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
                        binding.recyclerViewPopular.adapter = PopularAdapter(itemsModelList)
                    }
                }
            }
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE

        viewModel.loadPopular().observeForever { popularList ->
            val itemsModelList = popularList.map { popular ->
                ItemsModel(
                    title = popular.title,
                    description = popular.description,
                    picUrl = ArrayList(popular.picUrl),
                    price = popular.price,
                    rating = popular.rating,
                    extra = popular.extra
                )
            }.toMutableList()

            binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewPopular.adapter = PopularAdapter(itemsModelList)
            binding.progressBarPopular.visibility = View.GONE
        }

        viewModel.loadPopular()
    }

    private fun setupClickListeners() {
        binding.explorerBtn.setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        binding.orderBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupSearch() {
        // Xử lý sự kiện nhấn Enter trên EditText
        binding.editTextText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Xử lý click vào icon tìm kiếm
        binding.searchIcon.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val query = binding.editTextText.text.toString().trim()
        if (query.isNotEmpty()) {
            val intent = Intent(this, SearchResultsActivity::class.java)
            intent.putExtra("searchQuery", query)
            startActivity(intent)
        }
    }
}