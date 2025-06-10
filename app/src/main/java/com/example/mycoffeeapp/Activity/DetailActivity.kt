package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Helper.ManagmentFavorite
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.auth.FirebaseAuth

class DetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityDetailBinding
    private lateinit var  item:ItemsModel
    private lateinit var managementCart:ManagmentCart
    private lateinit var managmentFavorite: ManagmentFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managementCart=ManagmentCart(this)
        managmentFavorite = ManagmentFavorite(this)


        bundle()
        initSizeList()

        // Cập nhật icon yêu thích dựa vào trạng thái isFavorite
//        updateFavoriteIcon()

        // Xử lý sự kiện nút yêu thích (favBtn)
        binding.favBtn.setOnClickListener {
            toggleFavorite()
        }

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
            item=intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(binding.picMain)

            titleTxt.text=item.title
            descriptionTxt.text=item.description
            priceTxt.text=item.price.toString()+" VND"
            ratingtxt.text=item.rating.toString()

            addToCartBtn.setOnClickListener {
                item.numberInCart=Integer.valueOf(
                    numberItemTxt.text.toString()
                )
                managementCart.insertItems(item)
            }
            backBtn.setOnClickListener{
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            }
            plusCart.setOnClickListener {
                numberItemTxt.text=(item.numberInCart+1).toString()
                item.numberInCart++
            }
            minusBtn.setOnClickListener {
                if(item.numberInCart>0) {
                    numberItemTxt.text = (item.numberInCart - 1).toString()
                    item.numberInCart--
                }
            }
        }
    }
//    private fun updateFavoriteIcon() {
//        // Nếu món hàng đã được yêu thích, hiển thị icon yêu thích đầy
//        if (item.isFavorite) {
//            binding.favBtn.setImageResource(R.drawable.tim_do) // Icon đầy khi yêu thích
//        } else {
//            binding.favBtn.setImageResource(R.drawable.btn_3) // Icon rỗng khi không yêu thích
//        }
//    }

    fun toggleFavorite() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            // managmentFavorite.toggleFavorite đã tự động cập nhật item.isFavorite bên trong
            managmentFavorite.toggleFavorite(item, uid)

            // Cập nhật giao diện người dùng dựa trên trạng thái đã được cập nhật của item.isFavorite
//            updateFavoriteIcon()

            // Hiển thị Toast dựa trên trạng thái đã được cập nhật của item.isFavorite
            if (item.isFavorite) {
                Toast.makeText(this, "${item.title} đã được thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "${item.title} đã được xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng yêu thích", Toast.LENGTH_SHORT).show()
        }
    }
}