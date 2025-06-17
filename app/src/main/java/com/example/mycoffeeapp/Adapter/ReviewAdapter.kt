package com.example.mycoffeeapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycoffeeapp.Domain.RatingModel
import com.example.mycoffeeapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private val reviews: List<RatingModel>) : 
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTxt: TextView = itemView.findViewById(R.id.userNameTxt)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)
        val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        holder.apply {
            userNameTxt.text = review.userName
            ratingBar.rating = review.rating.toFloat()
            commentTxt.text = review.comment.takeIf { it.isNotEmpty() } ?: "Không có nhận xét"
            
            // Format date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateTxt.text = dateFormat.format(review.date)
        }
    }

    override fun getItemCount(): Int = reviews.size
} 