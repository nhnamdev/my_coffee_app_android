package com.example.mycoffeeapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycoffeeapp.Activity.ManageUsersActivity
import com.example.mycoffeeapp.databinding.ViewholderUserBinding

class UserAdapter(
    private val users: List<ManageUsersActivity.User>,
    private val onActionClick: (ManageUsersActivity.User, String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ViewholderUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ManageUsersActivity.User) {
            binding.usernameTxt.text = user.username
            binding.emailTxt.text = user.email
            binding.adminStatusTxt.text = if (user.isAdmin) "Admin" else "User"
            binding.btnDelete.setOnClickListener { onActionClick(user, "delete") }
            binding.btnToggleAdmin.setOnClickListener { onActionClick(user, "toggleAdmin") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ViewholderUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}