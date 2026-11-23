package com.example.myfoody.ui.fragments.recipes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.models.Result
import com.example.myfoody.ui.theme.MyFoodyTheme
import com.example.myfoody.ui.theme.RecipeCard
import com.google.android.material.color.MaterialColors

@Composable
fun RecipesScreen(
    recipes: List<Result>,
    loading: Boolean,
    error: String?,
    openRecipe: (Result) -> Unit,
    openFilters: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(colorResource(R.color.backgroundColor))) {
        when {
            loading -> RecipePlaceholders()
            error != null -> Column(
                Modifier.align(Alignment.TopCenter)
                    .offset(y = ((maxHeight - 100.dp) * 0.37f).coerceAtLeast(0.dp)).alpha(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painterResource(R.drawable.ic_sad), null, Modifier.size(100.dp), tint = colorResource(R.color.itemColor))
                Spacer(Modifier.height(8.dp))
                Text(error, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.descriptionColor))
            }
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeCard(recipe, onClick = { openRecipe(recipe) })
                }
            }
        }
        FloatingActionButton(
            onClick = openFilters,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            backgroundColor = Color(MaterialColors.getColor(LocalContext.current,
                com.google.android.material.R.attr.colorSecondary, "RecipesScreen"))
        ) {
            Icon(painterResource(R.drawable.ic_restaurant), stringResource(R.string.meal_type), tint = Color.White)
        }
    }
}

@Composable
private fun RecipePlaceholders() {
    BoxWithConstraints(Modifier.fillMaxSize().semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }) {
        val progress by rememberInfiniteTransition().animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
        )
        val imageWidth = minOf(200.dp, (maxWidth - 140.dp).coerceAtLeast(0.dp))
        val width = with(LocalDensity.current) { maxWidth.toPx() }
        val start = width * (2 * progress - 1)
        val base = colorResource(R.color.placeholderColor)
        val brush = Brush.linearGradient(
            listOf(base.copy(alpha = 0.3f), base, base.copy(alpha = 0.3f)),
            start = Offset(start, 0f), end = Offset(start + width.coerceAtLeast(1f), width / 4)
        )
        Column {
            repeat(4) {
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).height(200.dp)) {
                    Spacer(Modifier.width(imageWidth).fillMaxHeight().background(brush))
                    Column(Modifier.padding(12.dp)) {
                        Spacer(Modifier.fillMaxWidth().height(50.dp).background(brush))
                        Spacer(Modifier.height(4.dp))
                        Spacer(Modifier.fillMaxWidth().height(65.dp).background(brush))
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            repeat(3) { Spacer(Modifier.size(36.dp).background(brush)) }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipesErrorPreview() {
    MyFoodyTheme { RecipesScreen(emptyList(), false, "No Internet Connection.", {}, {}) }
}
