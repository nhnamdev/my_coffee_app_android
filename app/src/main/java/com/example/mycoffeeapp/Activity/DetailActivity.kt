package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Domain.RatingModel
import com.example.mycoffeeapp.Helper.ManagmentFavorite
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.pm.PackageManager
import android.net.Uri
import com.example.mycoffeeapp.Adapter.ReviewAdapter
import java.util.*

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managementCart: ManagmentCart
    private lateinit var managmentFavorite: ManagmentFavorite
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managementCart = ManagmentCart(this)
        managmentFavorite = ManagmentFavorite(this)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        bundle()
        initSizeList()
        setupClickListeners()
        loadRatings()
    }

    private fun initSizeList() {
        binding.apply {
            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }
            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                largeBtn.setBackgroundResource(0)
            }
            largeBtn.setOnClickListener {
                mediumBtn.setBackgroundResource(0)
                smallBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
            }
        }
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text = item.price.toString() + " VND"
            ratingtxt.text = item.rating.toString()

            // Kiểm tra trạng thái yêu thích và cập nhật icon
            updateFavoriteIcon()

            addToCartBtn.setOnClickListener {
                item.numberInCart = Integer.valueOf(
                    numberItemTxt.text.toString()
                )
                managementCart.insertItems(item)
            }
            backBtn.setOnClickListener {
                finish()
            }
            plusCart.setOnClickListener {
                numberItemTxt.text = (item.numberInCart + 1).toString()
                item.numberInCart++
            }
            minusBtn.setOnClickListener {
                if (item.numberInCart > 0) {
                    numberItemTxt.text = (item.numberInCart - 1).toString()
                    item.numberInCart--
                }
            }

            // Thêm xử lý nút chia sẻ
            shareBtn.setOnClickListener {
                showShareOptions()
            }

            // Thêm xử lý nút đánh giá
            ratingLayout.setOnClickListener {
                showRatingDialog()
            }
        }
    }

    private fun updateFavoriteIcon() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Kiểm tra trạng thái yêu thích từ Firestore
            firestore.collection("favorites")
                .document("${userId}_${item.title}")
                .get()
                .addOnSuccessListener { document ->
                    item.isFavorite = document.exists()
                    binding.favBtn.setImageResource(
                        if (item.isFavorite) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_outline
                    )
                }
        }
    }

    private fun toggleFavorite() {
        val userId = auth.currentUser?.uid
        Log.d("DetailActivity", "Toggle favorite - UserId: $userId")
        Log.d("DetailActivity", "Toggle favorite - Item: ${item.title}")

        userId?.let { uid ->
            managmentFavorite.toggleFavorite(item, uid)
            Log.d("DetailActivity", "After toggle - Item isFavorite: ${item.isFavorite}")

            // Cập nhật icon ngay sau khi thay đổi trạng thái
            binding.favBtn.setImageResource(
                if (item.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )

            if (item.isFavorite) {
                Toast.makeText(this, "${item.title} đã được thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "${item.title} đã được xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng yêu thích", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showShareOptions() {
        val shareMessage = """
            ${item.title}
            Giá: ${item.price} VND
            Mô tả: ${item.description}
            Đánh giá: ${item.rating}/5
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(intent, "Chia sẻ qua"))
    }

    private fun showRatingDialog() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá sản phẩm", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val commentInput = dialogView.findViewById<TextInputEditText>(R.id.commentInput)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        submitButton.setOnClickListener {
            val rating = ratingBar.rating.toDouble()
            val comment = commentInput.text.toString()

            if (rating == 0.0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitRating(rating, comment)
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun submitRating(rating: Double, comment: String) {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "Người dùng ẩn danh"

        val ratingModel = RatingModel(
            id = UUID.randomUUID().toString(),
            userId = userId,
            userName = userName,
            productId = item.title, // Sử dụng title làm productId tạm thời
            rating = rating,
            comment = comment,
            date = Date()
        )

        firestore.collection("ratings")
            .document(ratingModel.id)
            .set(ratingModel)
            .addOnSuccessListener {
                Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show()
                updateProductRating()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRatings() {
        firestore.collection("ratings")
            .whereEqualTo("productId", item.title)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var totalRating = 0.0
                    val reviews = mutableListOf<RatingModel>()
                    
                    for (document in documents) {
                        val rating = document.getDouble("rating") ?: 0.0
                        totalRating += rating
                        
                        // Convert Firestore document to RatingModel
                        val review = document.toObject(RatingModel::class.java)
                        reviews.add(review)
                    }
                    
                    val averageRating = totalRating / documents.size()
                    item.rating = averageRating
                    binding.ratingtxt.text = String.format("%.1f", averageRating)
                    
                    // Sort reviews by date (newest first)
                    reviews.sortByDescending { it.date }
                    
                    // Update UI
                    if (reviews.isNotEmpty()) {
                        binding.reviewsRecyclerView.apply {
                            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@DetailActivity)
                            adapter = ReviewAdapter(reviews)
                        }
                        binding.noReviewsTxt.visibility = android.view.View.GONE
                        binding.reviewsRecyclerView.visibility = android.view.View.VISIBLE
                    } else {
                        binding.noReviewsTxt.visibility = android.view.View.VISIBLE
                        binding.reviewsRecyclerView.visibility = android.view.View.GONE
                    }
                } else {
                    binding.noReviewsTxt.visibility = android.view.View.VISIBLE
                    binding.reviewsRecyclerView.visibility = android.view.View.GONE
                }
            }
            .addOnFailureListener { e ->
                Log.e("DetailActivity", "Error loading ratings: ${e.message}")
                binding.noReviewsTxt.visibility = android.view.View.VISIBLE
                binding.reviewsRecyclerView.visibility = android.view.View.GONE
            }
    }

    private fun updateProductRating() {
        loadRatings() // Cập nhật rating hiển thị
        
        // Cập nhật rating trong Firestore
        firestore.collection("products")
            .document(item.title)
            .update("rating", item.rating)
            .addOnFailureListener { e ->
                Log.e("DetailActivity", "Error updating product rating: ${e.message}")
            }
    }

    private fun setupClickListeners() {
        binding.favBtn.setOnClickListener {
            toggleFavorite()
        }
    }
}