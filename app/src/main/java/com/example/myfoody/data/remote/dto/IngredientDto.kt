package com.example.myfoody.data.remote.dto

import com.google.gson.annotations.SerializedName

data class IngredientDto(
    @SerializedName("amount")
    val amount: Double = 0.0,
    @SerializedName("consistency")
    val consistency: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("original")
    val original: String? = null,
    @SerializedName("unit")
    val unit: String? = null
)
