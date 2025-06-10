package com.example.mycoffeeapp.Helper

import android.content.Context
import android.util.Log
// import android.widget.Toast // Đã bỏ import Toast
import com.example.mycoffeeapp.Domain.ItemsModel

class ManagmentFavorite(val context: Context) {

    private val tinyDB = TinyDB(context)

    // Cập nhật trạng thái yêu thích của món hàng
    fun toggleFavorite(item: ItemsModel, userId: String) {
        val favoriteItems = getFavoriteItems(userId)
        Log.d("ManagmentFavorite", "Before toggle - User: $userId, Item: ${item.title}")
        val index = favoriteItems.indexOfFirst { it.title == item.title }
        if (index != -1) {
            favoriteItems.removeAt(index)
            item.isFavorite = false
        } else {
            item.isFavorite = true
            favoriteItems.add(item)
        }
        Log.d("ManagmentFavorite", "After toggle - Items: $favoriteItems")
        tinyDB.putListObject("FavoriteList_$userId", favoriteItems)
    }

    fun getFavoriteItems(userId: String): ArrayList<ItemsModel> {
        val items = tinyDB.getListObject("FavoriteList_$userId") ?: arrayListOf()
        Log.d("ManagmentFavorite", "Retrieved items for userId: $userId, items: $items")
        return items
    }
}