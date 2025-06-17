package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.ItemAdminAdapter
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.databinding.ActivityManageItemsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ManageItemsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageItemsBinding
    private lateinit var firestore: FirebaseFirestore
    private val items = mutableListOf<ItemsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        initItemList()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAddItem.setOnClickListener {
            // Simple implementation: Add a new item with default values
            val newItem = ItemsModel(
                title = binding.editItemTitle.text.toString(),
                description = binding.editItemDescription.text.toString(),
                picUrl = arrayListOf("default_image_url"),
                price = binding.editItemPrice.text.toString().toDoubleOrNull() ?: 0.0,
                rating = 0.0,
                numberInCart = 0,
                extra = ""
            )
            addItem(newItem)
        }
    }

    private fun initItemList() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        firestore.collection("items")
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                for (document in result) {
                    val item = ItemsModel(
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        picUrl = document.get("picUrl") as? ArrayList<String> ?: arrayListOf(),
                        price = document.getDouble("price") ?: 0.0,
                        rating = document.getDouble("rating") ?: 0.0,
                        numberInCart = document.getLong("numberInCart")?.toInt() ?: 0,
                        extra = document.getString("extra") ?: ""
                    )
                    items.add(item)
                }
                binding.recyclerViewItems.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewItems.adapter = ItemAdminAdapter(items) { item, action ->
                    when (action) {
                        "delete" -> deleteItem(item)
                        "edit" -> editItem(item)
                    }
                }
                binding.progressBar.visibility = android.view.View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi tải danh sách món: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = android.view.View.GONE
            }
    }

    private fun addItem(item: ItemsModel) {
        firestore.collection("items")
            .add(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã thêm ${item.title}", Toast.LENGTH_SHORT).show()
                initItemList()
                clearInputFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi thêm món: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteItem(item: ItemsModel) {
        firestore.collection("items")
            .whereEqualTo("title", item.title)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    firestore.collection("items").document(document.id).delete()
                }
                Toast.makeText(this, "Đã xóa ${item.title}", Toast.LENGTH_SHORT).show()
                initItemList()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi xóa món: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editItem(item: ItemsModel) {
        // Populate input fields for editing
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
            firestore.collection("items")
                .whereEqualTo("title", item.title)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        firestore.collection("items").document(document.id)
                            .set(updatedItem)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Đã cập nhật ${item.title}", Toast.LENGTH_SHORT).show()
                                initItemList()
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
                                        extra = ""
                                    ))
                                }
                            }
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