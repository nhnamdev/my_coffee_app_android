package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.widget.Toast // Thêm import Toast
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

        // Nếu danh sách rỗng, có thể hiển thị một thông báo hoặc giữ nguyên giao diện
        if (favoriteItems.isEmpty()) {
            Toast.makeText(this, "Danh sách yêu thích trống.", Toast.LENGTH_SHORT).show()
            // Bạn có thể hiển thị một TextView "Danh sách trống" và ẩn RecyclerView ở đây
            // binding.emptyListMessage.visibility = View.VISIBLE
            // binding.recyclerViewFavorites.visibility = View.GONE
        }

        var adapter = FavoriteAdapter(favoriteItems, this, managmentFavorite, userId)

        binding.recyclerViewFavorites.apply { // Sử dụng apply để gom các cài đặt RecyclerView
            layoutManager = LinearLayoutManager(this@FavoriteActivity) // Sử dụng this@FavoriteActivity
            adapter = adapter
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish() // Quay lại màn hình trước đó
        }
    }
}