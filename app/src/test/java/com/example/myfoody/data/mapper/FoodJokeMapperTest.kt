package com.example.myfoody.data.mapper

import com.example.myfoody.data.remote.dto.FoodJokeDto
import com.example.myfoody.domain.model.FoodJoke
import org.junit.Assert.assertEquals
import org.junit.Test

class FoodJokeMapperTest {

    @Test
    fun `food joke maps cleanly between dto entity and domain`() {
        val dto = FoodJokeDto(text = "Why did the tomato blush?")
        val domain = dto.toDomain()

        assertEquals(FoodJoke("Why did the tomato blush?"), domain)
        assertEquals(dto, domain.toDto())

        val entity = domain.toEntity()
        assertEquals(0, entity.id)
        assertEquals(domain, entity.toDomain())
    }
}
