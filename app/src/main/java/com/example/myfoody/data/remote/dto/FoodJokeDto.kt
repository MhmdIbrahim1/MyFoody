package com.example.myfoody.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodJokeDto(
    @SerializedName("text")
    val text: String
)
