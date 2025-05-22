package com.example.mycoffeeapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mycoffeeapp.Domain.Banner
import com.example.mycoffeeapp.Domain.Category
import com.example.mycoffeeapp.Domain.Items
import com.example.mycoffeeapp.Domain.Popular
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainRepository {
    private val database = FirebaseDatabase.getInstance()
    private val bannerRef = database.getReference("Banner")
    private val categoryRef = database.getReference("Category")
    private val itemsRef = database.getReference("Items")
    private val popularRef = database.getReference("Popular")

    private val _bannerList = MutableLiveData<List<Banner>>()
    val bannerList: LiveData<List<Banner>> = _bannerList

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    private val _itemsList = MutableLiveData<List<Items>>()
    val itemsList: LiveData<List<Items>> = _itemsList

    private val _popularList = MutableLiveData<List<Popular>>()
    val popularList: LiveData<List<Popular>> = _popularList

    fun loadBanner() {
        bannerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bannerList = mutableListOf<Banner>()
                for (bannerSnapshot in snapshot.children) {
                    val banner = bannerSnapshot.getValue(Banner::class.java)
                    banner?.let { bannerList.add(it) }
                }
                _bannerList.value = bannerList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadCategory() {
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList = mutableListOf<Category>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { categoryList.add(it) }
                }
                _categoryList.value = categoryList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadItems() {
        itemsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<Items>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(Items::class.java)
                    item?.let { itemsList.add(it) }
                }
                _itemsList.value = itemsList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadPopular() {
        popularRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val popularList = mutableListOf<Popular>()
                for (popularSnapshot in snapshot.children) {
                    val popular = popularSnapshot.getValue(Popular::class.java)
                    popular?.let { popularList.add(it) }
                }
                _popularList.value = popularList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadItemCategory(categoryId: String): LiveData<List<Items>> {
        val itemsLiveData = MutableLiveData<List<Items>>()
        val query: Query = itemsRef.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Items>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(Items::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return itemsLiveData
    }
}