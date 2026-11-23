package com.example.myfoody.presentation.favorites

import com.example.myfoody.domain.model.FavoriteRecipe

data class FavoritesUiState(
    val favorites: List<FavoriteRecipe> = emptyList(),
    val selectedIds: Set<Int> = emptySet()
) {
    val isSelectionMode: Boolean
        get() = selectedIds.isNotEmpty()

    val selectionTitle: String
        get() = when (selectedIds.size) {
            0 -> ""
            1 -> "1 item selected"
            else -> "${selectedIds.size} items selected"
        }
}
