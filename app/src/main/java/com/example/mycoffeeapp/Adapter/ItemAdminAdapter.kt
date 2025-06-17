package com.example.mycoffeeapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.databinding.ViewholderItemAdminBinding

class ItemAdminAdapter(
    private val items: List<ItemsModel>,
    private val onActionClick: (ItemsModel, String) -> Unit
) : RecyclerView.Adapter<ItemAdminAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ViewholderItemAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsModel) {
            binding.titleTxt.text = item.title
            binding.descriptionTxt.text = item.description
            binding.priceTxt.text = "${item.price} VND"
            Glide.with(binding.root.context)
                .load(item.picUrl.firstOrNull())
                .into(binding.picItem)
            binding.btnEdit.setOnClickListener { onActionClick(item, "edit") }
            binding.btnDelete.setOnClickListener { onActionClick(item, "delete") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ViewholderItemAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}