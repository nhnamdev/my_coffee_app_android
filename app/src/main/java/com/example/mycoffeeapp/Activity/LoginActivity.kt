package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private var captchaText: String = ""
    private var captchaAttempts: Int = 0
    private val maxCaptchaAttempts: Int = 3

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }

    private fun generateCaptcha(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    private fun refreshCaptcha() {
        captchaText = generateCaptcha()
        binding.captchaText.text = captchaText
        binding.captchaInput.setText("")
    }

    private fun validateCaptcha(): Boolean {
        val userInput = binding.captchaInput.text?.toString() ?: ""
        if (userInput.equals(captchaText, ignoreCase = true)) {
            captchaAttempts = 0
            return true
        }
        captchaAttempts++
        if (captchaAttempts >= maxCaptchaAttempts) {
            Toast.makeText(this, "Quá nhiều lần nhập sai captcha. Vui lòng thử lại sau.", Toast.LENGTH_LONG).show()
            binding.loginBtn.isEnabled = false
            binding.captchaInput.isEnabled = false
            return false
        }
        refreshCaptcha()
        Toast.makeText(this, "Captcha không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("136962826431-ig6rdqv83uvecife3ju0nl00hb1sfoeu.apps.googleusercontent.com") // Thay thế bằng Web Client ID từ Firebase Console
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            // Check if default account exists
            checkDefaultAccount()

            // Initialize captcha
            refreshCaptcha()

            binding.refreshCaptchaBtn.setOnClickListener {
                refreshCaptcha()
            }

            binding.loginBtn.setOnClickListener {
                val email = binding.emailEdt.text.toString()
                val password = binding.passwordEdt.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (!validateCaptcha()) {
                        return@setOnClickListener
                    }
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
                                refreshCaptcha()
                            }
                        }
                } else {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                }
            }

            binding.googleSignInButton.setOnClickListener {
                signInWithGoogle()
            }

            binding.registerBtn.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Lỗi khi khởi tạo Firebase: ${e.message}")
            Toast.makeText(this, "Lỗi khi khởi tạo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("LoginActivity", "Google Sign In Success: ${account.email}")

            val idToken = account.idToken
            if (idToken == null) {
                Log.e("LoginActivity", "ID Token is null")
                Toast.makeText(this, "Lỗi: Không lấy được ID Token", Toast.LENGTH_SHORT).show()
                return
            }

            firebaseAuthWithGoogle(idToken)
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Google sign in failed", e)
            Toast.makeText(this, "Đăng nhập Google thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Kiểm tra xem người dùng đã tồn tại trong Firestore chưa
                    firestore.collection("users")
                        .document(user?.uid ?: "")
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Nếu người dùng đã tồn tại, chuyển đến màn hình chính
                                startActivity(Intent(this, SplashActivity::class.java))
                                finish()
                            } else {
                                // Nếu người dùng chưa tồn tại, tạo mới thông tin
                                val userInfo = hashMapOf(
                                    "username" to (user?.displayName ?: ""),
                                    "email" to (user?.email ?: ""),
                                    "phone" to "",
                                    "address" to "",
                                    "photoUrl" to (user?.photoUrl?.toString() ?: ""),
                                    "createdAt" to System.currentTimeMillis()
                                )
                                firestore.collection("users")
                                    .document(user?.uid ?: "")
                                    .set(userInfo)
                                    .addOnSuccessListener {
                                        Log.d("LoginActivity", "Đã tạo thông tin người dùng mới thành công")
                                        startActivity(Intent(this, SplashActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("LoginActivity", "Lỗi khi tạo thông tin người dùng: ${e.message}")
                                        Toast.makeText(this, "Lỗi khi tạo thông tin người dùng: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Lỗi khi kiểm tra thông tin người dùng: ${e.message}")
                            Toast.makeText(this, "Lỗi khi kiểm tra thông tin người dùng: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
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