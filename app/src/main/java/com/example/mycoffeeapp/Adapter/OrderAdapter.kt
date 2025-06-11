package com.example.mycoffeeapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycoffeeapp.Domain.OrderModel
import com.example.mycoffeeapp.databinding.ViewholderOrderBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val orders: ArrayList<OrderModel>,
    private val context: Context
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ViewholderOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        holder.binding.apply {
            orderIdTxt.text = "Order #${order.orderId.take(8)}"
            orderDateTxt.text = "Date: ${dateFormat.format(order.orderDate)}"
            orderStatusTxt.text = "Status: ${order.status}"
            totalAmountTxt.text = "Total: ${order.totalAmount} VND"

            // Setup nested RecyclerView for order items
            orderItemsView.layoutManager = LinearLayoutManager(context)
            orderItemsView.adapter = CartAdapter(order.items, context)
        }
    }

    override fun getItemCount(): Int = orders.size
} 