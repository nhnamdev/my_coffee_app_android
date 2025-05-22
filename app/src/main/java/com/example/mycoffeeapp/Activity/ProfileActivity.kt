package com.example.mycoffeeapp.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ActivityProfileBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.profileImage.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.changePhotoText.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            updateUserProfile()
        }

        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.nameEdit.setText(document.getString("name"))
                    binding.emailEdit.setText(document.getString("email"))
                    binding.phoneEdit.setText(document.getString("phone"))
                    binding.addressEdit.setText(document.getString("address"))

                    // Load profile image
                    document.getString("profileImageUrl")?.let { imageUrl ->
                        Glide.with(this)
                            .load(imageUrl)
                            .into(binding.profileImage)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi tải thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        val name = binding.nameEdit.text.toString()
        val phone = binding.phoneEdit.text.toString()
        val address = binding.addressEdit.text.toString()

        if (name.isEmpty()) {
            binding.nameEdit.error = "Vui lòng nhập họ tên"
            return
        }

        val userData = hashMapOf(
            "name" to name,
            "phone" to phone,
            "address" to address
        )

        // Upload image if selected
        selectedImageUri?.let { uri ->
            val imageRef = storage.reference.child("profile_images/${UUID.randomUUID()}")
            imageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    imageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        userData["profileImageUrl"] = downloadUri.toString()
                        updateUserData(userId, userData)
                    } else {
                        Toast.makeText(this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
                    }
                }
        } ?: updateUserData(userId, userData)
    }

    private fun updateUserData(userId: String, userData: HashMap<String, String>) {
        firestore.collection("users").document(userId)
            .update(userData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi cập nhật: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordEdit = dialogView.findViewById<TextInputEditText>(R.id.currentPasswordEdit)
        val newPasswordEdit = dialogView.findViewById<TextInputEditText>(R.id.newPasswordEdit)
        val confirmPasswordEdit = dialogView.findViewById<TextInputEditText>(R.id.confirmPasswordEdit)

        AlertDialog.Builder(this)
            .setTitle("Đổi mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Đổi mật khẩu") { _, _ ->
                val currentPassword = currentPasswordEdit.text.toString()
                val newPassword = newPasswordEdit.text.toString()
                val confirmPassword = confirmPasswordEdit.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Reauthenticate user before changing password
                val user = auth.currentUser
                val credential = com.google.firebase.auth.EmailAuthProvider
                    .getCredential(user?.email ?: "", currentPassword)

                user?.reauthenticate(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Lỗi khi đổi mật khẩu: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
} 