package com.example.mycoffeeapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mycoffeeapp.Domain.CategoryModel
import com.example.mycoffeeapp.R
import com.example.mycoffeeapp.databinding.ViewholderCategoryBinding

class CategoryAdapter(val items:MutableList<CategoryModel>)
    :RecyclerView.Adapter<CategoryAdapter.Viewholder>() {
        private lateinit var context: Context
        private var selectedPosition=-1
        private var lastSelectedPosition=-1


    inner class Viewholder(val binding:ViewholderCategoryBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.Viewholder {
        context = parent.context
        val binding=ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.Viewholder, @SuppressLint("RecyclerView") position: Int) {
        val item=items[position]
        holder.binding.titleCat.text=item.title

        holder.binding.root.setOnClickListener{
            lastSelectedPosition=selectedPosition
            selectedPosition=position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
        }
        if(selectedPosition== position) {
            holder.binding.titleCat.setBackgroundResource(R.drawable.dark_brown_bg)
            holder.binding.titleCat.setTextColor(context.resources.getColor(R.color.white))
        }else{
            holder.binding.titleCat.setBackgroundResource(R.drawable.white_bg)
            holder.binding.titleCat.setTextColor(context.resources.getColor(R.color.darkBrown))
        }
    }

    override fun getItemCount(): Int = items.size
}