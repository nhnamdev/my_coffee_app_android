package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.FavoriteAdapter
import com.example.mycoffeeapp.Helper.ManagmentFavorite
import com.example.mycoffeeapp.databinding.ActivityFavoriteBinding
import com.google.firebase.auth.FirebaseAuth

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var managmentFavorite: ManagmentFavorite
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo ManagmentFavorite
        managmentFavorite = ManagmentFavorite(this)

        // Lấy userId của người dùng đã đăng nhập và xử lý trường hợp rỗng
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            // Nếu userId rỗng, hiển thị Toast và kết thúc Activity hoặc chuyển hướng
            Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách yêu thích.", Toast.LENGTH_LONG).show()
            finish() // Đóng FavoriteActivity nếu không có userId
            return // Thoát khỏi onCreate để tránh chạy tiếp code gây lỗi
        }

        // Nếu userId không rỗng, tiếp tục khởi tạo RecyclerView
        initRecyclerView()
        setVariable()
    }

    override fun onResume() {
        super.onResume()
        // Đảm bảo userId đã được khởi tạo
        if (::userId.isInitialized) {
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        val favoriteItems = managmentFavorite.getFavoriteItems(userId)
        Log.d("FavoriteActivity", "UserId: $userId")
        Log.d("FavoriteActivity", "Favorite items size: ${favoriteItems.size}")
        Log.d("FavoriteActivity", "Favorite items: $favoriteItems")

        // Nếu danh sách rỗng, hiển thị thông báo
        if (favoriteItems.isEmpty()) {
            binding.emptyListMessage.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
            Toast.makeText(this, "Danh sách yêu thích trống.", Toast.LENGTH_SHORT).show()
        } else {
            binding.emptyListMessage.visibility = View.GONE
            binding.recyclerViewFavorites.visibility = View.VISIBLE
            
            val adapter = FavoriteAdapter(favoriteItems, this, managmentFavorite, userId)
            binding.recyclerViewFavorites.apply {
                layoutManager = LinearLayoutManager(this@FavoriteActivity)
                this.adapter = adapter
            }
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish() // Quay lại màn hình trước đó
        }
    }
}