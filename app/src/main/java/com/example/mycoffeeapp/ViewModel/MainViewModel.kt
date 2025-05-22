package com.example.mycoffeeapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mycoffeeapp.Domain.Banner
import com.example.mycoffeeapp.Domain.Category
import com.example.mycoffeeapp.Domain.Items
import com.example.mycoffeeapp.Domain.Popular
import com.example.mycoffeeapp.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadBanner(): LiveData<List<Banner>> {
        repository.loadBanner()
        return repository.bannerList
    }

    fun loadCategory(): LiveData<List<Category>> {
        repository.loadCategory()
        return repository.categoryList
    }

    fun loadItems(): LiveData<List<Items>> {
        repository.loadItems()
        return repository.itemsList
    }

    fun loadPopular(): LiveData<List<Popular>> {
        repository.loadPopular()
        return repository.popularList
    }

    fun loadItemCategory(categoryId: String): LiveData<List<Items>> {
        return repository.loadItemCategory(categoryId)
    }
}