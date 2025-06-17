package com.example.mycoffeeapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.databinding.ViewholderItemAdminBinding

class ItemAdminAdapter(
    private val items: List<ItemsModel>,
    private val onItemAction: (ItemsModel, String) -> Unit
) : RecyclerView.Adapter<ItemAdminAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderItemAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderItemAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text = "${item.price} VND"
            if (item.picUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(item.picUrl[0])
                    .into(picItem)
            }
            btnEdit.setOnClickListener { onItemAction(item, "edit") }
            btnDelete.setOnClickListener { onItemAction(item, "delete") }
        }
    }

    override fun getItemCount(): Int = items.size
}