package com.example.mycoffeeapp.Domain

import java.io.Serializable
import java.util.Date

data class OrderModel(
    var orderId: String = "",
    var items: ArrayList<ItemsModel> = ArrayList(),
    var totalAmount: Double = 0.0,
    var tax: Double = 0.0,
    var deliveryFee: Double = 0.0,
    var orderDate: Date = Date(),
    var status: String = "Completed" // Completed, Cancelled, etc.
): Serializable 