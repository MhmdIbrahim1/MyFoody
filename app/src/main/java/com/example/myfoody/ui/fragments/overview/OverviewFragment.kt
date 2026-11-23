package com.example.myfoody.ui.fragments.overview

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.models.Result
import com.example.myfoody.ui.theme.MyFoodyTheme
import com.example.myfoody.ui.theme.RecipeImage
import com.example.myfoody.ui.theme.recipeFont
import org.jsoup.Jsoup
import kotlin.math.abs

@Composable
fun OverviewScreen(recipe: Result?) {
    var expanded by rememberSaveable { mutableStateOf(1f) }
    val travel = with(LocalDensity.current) { 249.dp.toPx() }
    fun moveHeader(delta: Float): Float {
        val previous = expanded
        expanded = (previous + delta / travel).coerceIn(0f, 1f)
        return (expanded - previous) * travel
    }
    suspend fun settle(velocity: Float) {
        if (expanded == 0f || expanded == 1f) return
        val expand = if (abs(velocity) > travel) velocity > 0 else expanded >= 0.5f
        val target = if (expand) 1f else 0f
        animate(expanded, target, animationSpec = tween((1000 * abs(target - expanded)).toInt())) { value, _ ->
            expanded = value
        }
    }

    val scrollConnection = remember(travel) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset(0f, moveHeader(available.y.coerceAtMost(0f)))
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return Offset(0f, moveHeader(available.y.coerceAtLeast(0f)))
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (expanded == 0f || expanded == 1f) return Velocity.Zero
                settle(available.y)
                return Velocity(0f, available.y)
            }
        }
    }
    val dragState = rememberDraggableState { moveHeader(it) }
    Column(
        Modifier.fillMaxSize().background(colorResource(R.color.backgroundColor)).nestedScroll(scrollConnection)
    ) {
        Column(Modifier.draggable(dragState, Orientation.Vertical, onDragStopped = { settle(it) })) {
            OverviewHeader(recipe, expanded)
            Text(
                "Title", Modifier.padding(start = 6.dp, end = 12.dp, top = 20.dp),
                fontFamily = recipeFont(bold = true), fontWeight = FontWeight.Bold, fontSize = 22.sp,
                color = colorResource(R.color.titleColor)
            )
            Row(Modifier.fillMaxWidth().padding(start = 6.dp, top = 8.dp, end = 6.dp)) {
                FlagColumn(R.string.vegetarian, recipe?.vegetarian, R.string.vegan, recipe?.vegan)
                Spacer(Modifier.width(24.dp))
                FlagColumn(R.string.gluten_free, recipe?.glutenFree, R.string.dairy_free, recipe?.dairyFree)
                Spacer(Modifier.width(20.dp))
                FlagColumn(R.string.very_healthy, recipe?.veryHealthy, R.string.cheap, recipe?.cheap)
            }
        }
        Text(
            remember(recipe?.summary) { recipe?.summary?.let { Jsoup.parse(it).text() } ?: "TextView" },
            Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            color = colorResource(R.color.descriptionColor), fontSize = 16.sp
        )
    }
}

@Composable
private fun OverviewHeader(recipe: Result?, expanded: Float) {
    Box(
        Modifier.fillMaxWidth().height(1.dp + 249.dp * expanded).clipToBounds().alpha(expanded)
    ) {
        recipe?.let { RecipeImage(it.image, Modifier.matchParentSize(), useRowImageOptions = false) }
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(100.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))))
        )
        Row(
            Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeaderStat(R.drawable.ic_heart, recipe?.aggregateLikes ?: 100)
            HeaderStat(R.drawable.ic_clock, recipe?.readyInMinutes ?: 100)
        }
    }
}

@Composable
private fun HeaderStat(icon: Int, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painterResource(icon), null, tint = Color.White)
        Text(value.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun FlagColumn(firstLabel: Int, firstEnabled: Boolean?, secondLabel: Int, secondEnabled: Boolean?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DietaryFlag(firstLabel, firstEnabled == true)
        DietaryFlag(secondLabel, secondEnabled == true)
    }
}

@Composable
private fun DietaryFlag(label: Int, enabled: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(painterResource(R.drawable.ic_checkmark), null, tint = colorResource(if (enabled) R.color.green else R.color.itemColor))
        Text(stringResource(label), fontSize = 14.sp, color = colorResource(if (enabled) R.color.green else R.color.descriptionColor))
    }
}

@Preview(showBackground = true)
@Composable
private fun OverviewPreview() {
    MyFoodyTheme { OverviewScreen(null) }
}
