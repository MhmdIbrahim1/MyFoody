package com.example.myfoody.ui.fragments.foodJoke

import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myfoody.R
import com.example.myfoody.ui.theme.MyFoodyTheme
import com.example.myfoody.ui.theme.recipeFont

@Composable
internal fun FoodJokeScreen(joke: String?, loading: Boolean, error: String?) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) R.drawable.food_joke_background_dark else R.drawable.food_joke_background_light
    Box(Modifier.fillMaxSize()) {
        // Let Android render the original detailed background vector at the bounds of the screen.
        AndroidView(factory = { View(it) }, modifier = Modifier.matchParentSize(),
            update = { it.setBackgroundResource(background) })
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp).size(48.dp),
                color = colorResource(R.color.colorAccent)
            )
        } else if (joke != null) {
            Card(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = colorResource(R.color.cardBackgroundColor),
                border = BorderStroke(1.dp, colorResource(R.color.strokeColor))
            ) {
                Text(joke, modifier = Modifier.padding(16.dp), fontSize = 18.sp, fontFamily = recipeFont(),
                    color = colorResource(if (dark) R.color.titleColor else R.color.dark))
            }
        } else if (error != null) {
            Image(painterResource(R.drawable.ic_sad), contentDescription = null,
                modifier = Modifier.align(Alignment.Center).size(100.dp).alpha(0.5f))
            Column(Modifier.fillMaxSize().padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.fillMaxHeight(0.5f))
                Spacer(Modifier.height(58.dp))
                Text(error, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(0.5f))
            }
        }
    }
}

@Preview
@Composable
private fun FoodJokePreview() {
    MyFoodyTheme { FoodJokeScreen("A recipe for laughter.", false, null) }
}
