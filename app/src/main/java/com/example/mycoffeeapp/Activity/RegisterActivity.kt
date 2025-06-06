package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            val confirmPassword = binding.confirmPasswordEdit.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            binding.nameLayout.error = "Vui lòng nhập tên"
            return false
        }
        if (email.isEmpty()) {
            binding.emailLayout.error = "Vui lòng nhập email"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Vui lòng nhập password"
            return false
        }
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Vui lòng nhập comfirm password"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords k giống nhau"
            return false
        }
        if (password.length < 6) {
            binding.passwordLayout.error = "Password cần ít nhất 6 kí tự"
            return false
        }
        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Save additional user data to Firestore
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "phone" to "",
                        "address" to ""
                    )

                    db.collection("users")
                        .document(user!!.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            // Chuyển về màn hình đăng nhập
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Lỗi khi lưu thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lỗi đăng ký: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
} 