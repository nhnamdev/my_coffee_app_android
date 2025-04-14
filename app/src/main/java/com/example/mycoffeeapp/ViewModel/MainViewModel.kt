package com.example.mycoffeeapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mycoffeeapp.Domain.BannerModel
import com.example.mycoffeeapp.Domain.CategoryModel
import com.example.mycoffeeapp.Domain.ItemsModel
import com.example.mycoffeeapp.Repository.MainRepository

class MainViewModel:ViewModel() {
    private val repository = MainRepository()

    fun loadBanner():LiveData<MutableList<BannerModel>>{
        return repository.loadBanner()
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadPopular():LiveData<MutableList<ItemsModel>>{
        return repository.loadPopular()
    }
}