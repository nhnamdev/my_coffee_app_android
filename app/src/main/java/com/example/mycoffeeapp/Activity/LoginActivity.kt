package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            // Check if default account exists
            checkDefaultAccount()

            binding.loginBtn.setOnClickListener {
                val email = binding.emailEdt.text.toString()
                val password = binding.passwordEdt.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    binding.loginBtn.isEnabled = false
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            binding.loginBtn.isEnabled = true
                            if (task.isSuccessful) {
                                startActivity(Intent(this, SplashActivity::class.java))
                                finish()
                            } else {
                                val errorMessage = task.exception?.message ?: "Login failed"
                                Log.e("LoginActivity", "Login error: $errorMessage")
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }

            binding.registerBtn.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Lỗi khi khởi tạo Firebase: ${e.message}")
            Toast.makeText(this, "Lỗi khi khởi tạo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkDefaultAccount() {
        try {
            val defaultEmail = "namnguyen@gmail.com"
            val defaultPassword = "12345678"

            auth.signInWithEmailAndPassword(defaultEmail, defaultPassword)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Create default account if it doesn't exist
                        auth.createUserWithEmailAndPassword(defaultEmail, defaultPassword)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    // Save user info to Firestore
                                    val user = hashMapOf(
                                        "username" to "namnguyen",
                                        "email" to defaultEmail,
                                        "phone" to "",
                                        "address" to ""
                                    )
                                    firestore.collection("users")
                                        .document(auth.currentUser?.uid ?: "")
                                        .set(user)
                                        .addOnSuccessListener {
                                            Log.d("LoginActivity", "TK defaut đc tạo thanh công")
                                            Toast.makeText(this, "TK defaut đc tạo", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("LoginActivity", "Lỗi khi tạo tk mặc định: ${e.message}")
                                            Toast.makeText(this, "Lỗi khi tạo tk mặc định: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Log.e("LoginActivity", "Lỗi khi tạo tk mặc định: ${createTask.exception?.message}")
                                }
                            }
                    }
                }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Lỗi khi check tk mặc định: ${e.message}")
        }
    }
} 