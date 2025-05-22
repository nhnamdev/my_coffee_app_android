package com.example.mycoffeeapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycoffeeapp.Activity.DetailActivity
import com.example.mycoffeeapp.Domain.Items
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.databinding.ViewholderItemsBinding

class ItemsAdapter(private val items: List<Items>) : 
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ViewholderItemsBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Items) {
            binding.titleTxt.text = item.title
            binding.priceTxt.text = "$${item.price}"
            binding.ratingTxt.text = item.rating.toString()
            
            // Load image using Glide
            Glide.with(binding.root.context)
                .load(item.picUrl.firstOrNull())
                .into(binding.pic)

            // Set click listener
            binding.root.setOnClickListener {
                // Convert Items to ItemsModel
                val itemsModel = ItemsModel(
                    title = item.title,
                    description = item.description,
                    picUrl = ArrayList(item.picUrl),
                    price = item.price,
                    rating = item.rating,
                    extra = item.extra
                )

                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("object", itemsModel)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
} 