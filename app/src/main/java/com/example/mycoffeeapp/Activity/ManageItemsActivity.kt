package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.ItemAdminAdapter
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.databinding.ActivityManageItemsBinding
import com.google.firebase.database.*

class ManageItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageItemsBinding
    private lateinit var database: DatabaseReference
    private val items = mutableListOf<ItemsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance("https://mycoffeeapp-a6df3-default-rtdb.firebaseio.com/").getReference("Items")
        initItemList()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAddItem.setOnClickListener {
            val newItem = ItemsModel(
                title = binding.editItemTitle.text.toString(),
                description = binding.editItemDescription.text.toString(),
                picUrl = arrayListOf("default_image_url"),
                price = binding.editItemPrice.text.toString().toDoubleOrNull() ?: 0.0,
                rating = 0.0,
                numberInCart = 0,
                extra = "",
                categoryId = ""
            )
            addItem(newItem)
        }
    }

    private fun initItemList() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (dataSnapshot in snapshot.children) {
                    val item = dataSnapshot.getValue(ItemsModel::class.java)
                    item?.let { items.add(it) }
                }
                Log.d("ManageItemsActivity", "Số lượng items: ${items.size}")
                binding.recyclerViewItems.layoutManager = LinearLayoutManager(this@ManageItemsActivity)
                binding.recyclerViewItems.adapter = ItemAdminAdapter(items) { item, action ->
                    when (action) {
                        "delete" -> deleteItem(item)
                        "edit" -> editItem(item)
                    }
                }
                binding.progressBar.visibility = android.view.View.GONE
                if (items.isEmpty()) {
                    Toast.makeText(this@ManageItemsActivity, "Không có sản phẩm nào trong danh sách.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ManageItemsActivity", "Lỗi khi tải danh sách món: ${error.message}")
                Toast.makeText(this@ManageItemsActivity, "Lỗi khi tải danh sách món: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = android.view.View.GONE
            }
        })
    }

    private fun addItem(item: ItemsModel) {
        database.child(item.title).setValue(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã thêm ${item.title}", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi thêm món: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteItem(item: ItemsModel) {
        database.child(item.title).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xóa ${item.title}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi xóa món: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editItem(item: ItemsModel) {
        binding.editItemTitle.setText(item.title)
        binding.editItemDescription.setText(item.description)
        binding.editItemPrice.setText(item.price.toString())

        binding.btnAddItem.text = "Update Item"
        binding.btnAddItem.setOnClickListener {
            val updatedItem = item.copy(
                title = binding.editItemTitle.text.toString(),
                description = binding.editItemDescription.text.toString(),
                price = binding.editItemPrice.text.toString().toDoubleOrNull() ?: item.price
            )
            database.child(item.title).setValue(updatedItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã cập nhật ${item.title}", Toast.LENGTH_SHORT).show()
                    clearInputFields()
                    binding.btnAddItem.text = "Add Item"
                    binding.btnAddItem.setOnClickListener {
                        addItem(ItemsModel(
                            title = binding.editItemTitle.text.toString(),
                            description = binding.editItemDescription.text.toString(),
                            picUrl = arrayListOf("default_image_url"),
                            price = binding.editItemPrice.text.toString().toDoubleOrNull() ?: 0.0,
                            rating = 0.0,
                            numberInCart = 0,
                            extra = "",
                            categoryId = ""
                        ))
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi cập nhật món: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearInputFields() {
        binding.editItemTitle.setText("")
        binding.editItemDescription.setText("")
        binding.editItemPrice.setText("")
    }
}