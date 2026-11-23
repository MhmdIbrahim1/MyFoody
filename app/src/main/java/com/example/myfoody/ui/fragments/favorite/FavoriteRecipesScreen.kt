package com.example.myfoody.ui.fragments.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.ui.theme.MyFoodyTheme
import com.example.myfoody.ui.theme.RecipeCard

@Composable
internal fun FavoriteRecipesScreen(
    favorites: List<FavoritesEntity>,
    selection: Set<Int>,
    onClick: (FavoritesEntity) -> Unit,
    onLongClick: (FavoritesEntity) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(colorResource(R.color.backgroundColor))) {
        if (favorites.isEmpty()) {
            val imageSize = 100.dp
            val imageTop = ((maxHeight - imageSize) * 0.37f).coerceAtLeast(0.dp)
            Column(Modifier.fillMaxWidth().padding(top = imageTop).alpha(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(R.drawable.ic_menu_book), null, Modifier.size(imageSize))
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.no_favorites_recipes_yet), fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = colorResource(R.color.descriptionColor))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 8.dp)) {
                items(favorites, key = { it.id }) { favorite ->
                    RecipeCard(favorite.result, onClick = { onClick(favorite) }, selected = favorite.id in selection,
                        onLongClick = { onLongClick(favorite) })
                }
            }
        }
    }
}

@Preview
@Composable
private fun EmptyFavoritesPreview() {
    MyFoodyTheme { FavoriteRecipesScreen(emptyList(), emptySet(), {}, {}) }
}
