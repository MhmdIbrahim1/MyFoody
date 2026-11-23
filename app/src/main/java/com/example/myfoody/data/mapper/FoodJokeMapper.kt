package com.example.myfoody.data.mapper

import com.example.myfoody.data.local.database.entity.FoodJokeEntity
import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.domain.model.FoodJoke

fun FoodJokeDto.toDomain(): FoodJoke = FoodJoke(text = text)

fun FoodJoke.toDto(): FoodJokeDto = FoodJokeDto(text = text)

fun FoodJokeEntity.toDomain(): FoodJoke = foodJoke.toDomain()

fun FoodJoke.toEntity(): FoodJokeEntity = FoodJokeEntity(foodJoke = toDto())
