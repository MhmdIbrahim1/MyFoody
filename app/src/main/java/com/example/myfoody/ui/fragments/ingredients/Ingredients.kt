package com.example.myfoody.ui.fragments.ingredients

import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.myfoody.models.ExtendedIngredient
import com.example.myfoody.ui.theme.MyFoodyTheme
import com.example.myfoody.ui.theme.RecipeImage
import com.example.myfoody.ui.theme.recipeFont
import com.example.myfoody.util.Constants.Companion.BASE_IMAGE_URL
import java.util.Locale

@Composable
fun IngredientsScreen(ingredients: List<ExtendedIngredient>) {
    LazyColumn(Modifier.fillMaxSize().background(colorResource(R.color.backgroundColor))) {
        items(ingredients) { ingredient -> IngredientRow(ingredient) }
    }
}

@Composable
private fun IngredientRow(ingredient: ExtendedIngredient) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, colorResource(R.color.strokeColor)),
        backgroundColor = colorResource(R.color.cardBackgroundColor)
    ) {
        Box(Modifier.fillMaxWidth().heightIn(min = 100.dp)) {
            Box(Modifier.matchParentSize()) {
                Box(
                    Modifier.width(120.dp).fillMaxHeight().background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    RecipeImage(BASE_IMAGE_URL + ingredient.image, Modifier.size(100.dp), ImageView.ScaleType.FIT_CENTER)
                }
            }
            Column(
                modifier = Modifier.padding(start = 120.dp).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    ingredient.name.replaceFirstChar { it.titlecase(Locale.ROOT) },
                    fontSize = 20.sp, fontFamily = recipeFont(bold = true), fontWeight = FontWeight.Bold,
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
        IngredientsScreen(listOf(ExtendedIngredient(100.0, "Solid", "", "carrot", "1 chopped carrot", "grams")))
    }
}
