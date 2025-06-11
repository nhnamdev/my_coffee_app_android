package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
//import androidx.activity.R
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
        binding.progressBarBanner.visibility=View.VISIBLE
        viewModel.loadBanner().observeForever{
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
            // Convert Category to CategoryModel
            val categoryModelList = categoryList.map { category ->
                CategoryModel(
                    title = category.title,
                    id = category.id
                )
            }.toMutableList()

            binding.recyclerViewCat.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewCat.adapter = CategoryAdapter(categoryModelList)
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initPopular(){
        binding.progressBarPopular.visibility = View.VISIBLE

        viewModel.loadPopular().observeForever { popularList ->
            // Convert Popular to ItemsModel
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
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }


        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.favouriteBtn.setOnClickListener {
            // nếu vẫn không hiển thị dươc thì dùng cái này
//          Toast.makeText(this, "Chức năng yeu thich đang được phát triển", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, com.example.mycoffeeapp.Activity.FavoriteActivity::class.java))
        }

        binding.orderBtn.setOnClickListener {
            // TODO: Chuyển đến màn hình đơn hàng
            Toast.makeText(this, "Chức năng đơn hàng đang được phát triển", Toast.LENGTH_SHORT).show()
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
