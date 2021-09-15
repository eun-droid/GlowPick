package com.yes.glowpick.model.recommend

data class RecommendProduct(
    val idProduct: Int,
    val productTitle: String,
    val ratingAvg: Float,
    val reviewCount: String,
    val imageUrl: String
)
