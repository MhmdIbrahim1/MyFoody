package com.example.myfoody.ui.theme

import android.graphics.Typeface
import android.widget.ImageView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.example.myfoody.R

@Composable
fun recipeFont(bold: Boolean = false): FontFamily {
    val context = LocalContext.current
    var font by remember(context, bold) { mutableStateOf<FontFamily>(FontFamily.Default) }
    DisposableEffect(context, bold) {
        var active = true
        ResourcesCompat.getFont(context, R.font.courgette, object : ResourcesCompat.FontCallback() {
            override fun onFontRetrieved(typeface: Typeface) {
                if (active) font = FontFamily(if (bold) Typeface.create(typeface, Typeface.BOLD) else typeface)
            }
            override fun onFontRetrievalFailed(reason: Int) = Unit
        }, null)
        onDispose { active = false }
    }
    return font
}

@Composable
fun RecipeImage(url: String, modifier: Modifier = Modifier, scale: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP, useRowImageOptions: Boolean = true) {
    AndroidView(
        modifier = modifier,
        factory = { ImageView(it) },
        update = { image ->
            image.scaleType = scale
            if (image.tag != url) {
                image.tag = url
                image.load(url) {
                    if (useRowImageOptions) {
                        crossfade(600)
                        error(R.drawable.ic_error_placeholder)
                    }
                }
            }
        }
    )
}
