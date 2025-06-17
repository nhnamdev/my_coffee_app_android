package com.example.mycoffeeapp.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycoffeeapp.Adapter.UserAdapter
import com.example.mycoffeeapp.databinding.ActivityManageUsersBinding
import com.google.firebase.firestore.FirebaseFirestore

class ManageUsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var firestore: FirebaseFirestore
    private val users = mutableListOf<User>()

    data class User(
        val id: String,
        val username: String,
        val email: String,
        val isAdmin: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        initUserList()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initUserList() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users.clear()
                for (document in result) {
                    users.add(
                        User(
                            id = document.id,
                            username = document.getString("username") ?: "",
                            email = document.getString("email") ?: "",
                            isAdmin = document.getBoolean("isAdmin") ?: false
                        )
                    )
                }
                binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewUsers.adapter = UserAdapter(users) { user, action ->
                    when (action) {
                        "delete" -> deleteUser(user)
                        "toggleAdmin" -> toggleAdminStatus(user)
                    }
                }
                binding.progressBar.visibility = android.view.View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi tải danh sách người dùng: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = android.view.View.GONE
            }
    }

    private fun deleteUser(user: User) {
        firestore.collection("users")
            .document(user.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Đã xóa ${user.username}", Toast.LENGTH_SHORT).show()
                initUserList()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi xóa người dùng: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleAdminStatus(user: User) {
        firestore.collection("users")
            .document(user.id)
            .update("isAdmin", !user.isAdmin)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã cập nhật quyền admin cho ${user.username}", Toast.LENGTH_SHORT).show()
                initUserList()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi cập nhật quyền admin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}