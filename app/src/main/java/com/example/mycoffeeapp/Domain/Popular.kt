package com.example.mycoffeeapp.Domain

data class Popular(
    val description: String = "",
    val extra: String = "",
    val picUrl: List<String> = listOf(),
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val title: String = ""
) 