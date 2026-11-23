package com.example.myfoody.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.models.Result
import org.jsoup.Jsoup

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeCard(recipe: Result, onClick: () -> Unit, selected: Boolean = false, onLongClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, colorResource(if (selected) R.color.colorPrimary else R.color.strokeColor)),
        backgroundColor = colorResource(if (selected) R.color.cardBackgroundLightColor else R.color.cardBackgroundColor)
    ) {
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                .semantics { if (onLongClick != null) this.selected = selected }
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        ) {
            Box(Modifier.weight(0.45f).fillMaxHeight().clipToBounds()) {
                RecipeImage(recipe.image, Modifier.matchParentSize())
            }
            Column(Modifier.weight(0.55f).padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 18.dp)) {
                Text(recipe.title, color = colorResource(R.color.titleColor), fontSize = 20.sp,
                    fontWeight = FontWeight.Bold, fontFamily = recipeFont(bold = true), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(12.dp))
                Text(remember(recipe.summary) { Jsoup.parse(recipe.summary).text() },
                    color = colorResource(R.color.descriptionColor), fontSize = 14.sp,
                    maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RecipeStat(R.drawable.ic_heart, recipe.aggregateLikes.toString(), colorResource(R.color.red))
                    RecipeStat(R.drawable.ic_clock, recipe.readyInMinutes.toString(), colorResource(R.color.yellow))
                    RecipeStat(R.drawable.ic_leaf, "Vegan", colorResource(if (recipe.vegan) R.color.green else R.color.descriptionColor))
                }
            }
        }
    }
}

@Composable
private fun RecipeStat(icon: Int, value: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painterResource(icon), null, tint = tint)
        Text(value, color = tint, fontSize = 14.sp, maxLines = 1)
    }
}
