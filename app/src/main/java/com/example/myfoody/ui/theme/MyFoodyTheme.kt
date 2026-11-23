package com.example.myfoody.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.colorResource
import com.example.myfoody.R

@Composable
fun MyFoodyTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    MaterialTheme(
        colors = colors.copy(
            primary = colorResource(R.color.colorPrimary),
            secondary = colorResource(R.color.colorAccent),
            background = colorResource(R.color.backgroundColor),
            surface = colorResource(R.color.cardBackgroundColor),
            onSurface = colorResource(R.color.itemColor)
        ),
        content = { CompositionLocalProvider(LocalElevationOverlay provides null, content = content) }
    )
}
