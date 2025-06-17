package com.example.mycoffeeapp.Domain

import java.util.Date

data class RatingModel(
    var id: String = "",
    var userId: String = "",
    var userName: String = "",
    var productId: String = "",
    var rating: Double = 0.0,
    var comment: String = "",
    var date: Date = Date()
) 