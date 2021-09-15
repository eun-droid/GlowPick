package com.yes.glowpick.model.product


data class Product(
    val idProduct: Int,
    val idBrand: Int,
    val productTitle: String,
    val volume: String,
    val price: Int,
    val productScore: Float,
    val ratingAvg: Float,
    val productRank: String,
    val rankChange: String,
    val rankChangeType: String,
    val reviewCount: String,
    val imageUrl: String,
    val brand: Brand
)
