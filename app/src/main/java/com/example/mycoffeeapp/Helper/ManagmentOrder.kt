package com.example.mycoffeeapp.Helper

import android.content.Context
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Domain.OrderModel
import java.util.UUID
import java.util.ArrayList

class ManagmentOrder(val context: Context) {
    private val tinyDB = TinyDB(context)

    fun insertOrder(items: ArrayList<ItemsModel>, totalAmount: Double, tax: Double, deliveryFee: Double) {
        val orderList = getListOrder()
        val order = OrderModel(
            orderId = UUID.randomUUID().toString(),
            items = ArrayList(items),
            totalAmount = totalAmount,
            tax = tax,
            deliveryFee = deliveryFee
        )
        orderList.add(order)
        tinyDB.putOrderList("OrderList", orderList)
    }

    fun getListOrder(): ArrayList<OrderModel> {
        return tinyDB.getOrderList("OrderList")
    }
}
