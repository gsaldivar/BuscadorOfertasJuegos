package com.miapp.cheapshark.api

import com.google.gson.annotations.SerializedName

data class GameDeal(
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumbnailUrl: String,
    @SerializedName("salePrice") val salePrice: String,
    @SerializedName("normalPrice") val normalPrice: String,
    @SerializedName("storeID") val storeID: String,
    @SerializedName("steamRatingText") val steamRatingText: String?
)