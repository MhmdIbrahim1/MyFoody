package com.example.myfoody.presentation.details

import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.domain.model.Ingredient
import com.example.myfoody.presentation.components.RecipeImage
import com.example.myfoody.presentation.components.recipeFont
import com.example.myfoody.presentation.theme.MyFoodyTheme
import java.util.Locale

private const val INGREDIENT_IMAGE_BASE_URL = "https://spoonacular.com/cdn/ingredients_100x100/"

@Composable
fun IngredientsScreen(ingredients: List<Ingredient>) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.backgroundColor))
    ) {
        items(ingredients) { ingredient -> IngredientRow(ingredient) }
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, colorResource(R.color.strokeColor)),
        backgroundColor = colorResource(R.color.cardBackgroundColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        ) {
            Box(Modifier.matchParentSize()) {
                Box(
                    Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    RecipeImage(
                        url = INGREDIENT_IMAGE_BASE_URL + ingredient.image,
                        modifier = Modifier.size(100.dp),
                        scale = ImageView.ScaleType.FIT_CENTER
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 120.dp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = ingredient.name.replaceFirstChar { it.titlecase(Locale.ROOT) },
                    fontSize = 20.sp,
                    fontFamily = recipeFont(bold = true),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.titleColor)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IngredientText(ingredient.amount.toString())
                    IngredientText(ingredient.unit)
                }
                IngredientText(ingredient.consistency)
                IngredientText(ingredient.original)
            }
        }
    }
}

@Composable
private fun IngredientText(text: String) {
    Text(text, fontSize = 14.sp, color = colorResource(R.color.descriptionColor))
}

@Preview(showBackground = true)
@Composable
private fun IngredientsPreview() {
    MyFoodyTheme {
        IngredientsScreen(listOf(Ingredient(100.0, "Solid", "", "carrot", "1 chopped carrot", "grams")))
    }
}
