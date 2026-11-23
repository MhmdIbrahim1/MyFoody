package com.example.myfoody.ui.fragments.instructions

import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myfoody.ui.theme.MyFoodyTheme
import kotlin.math.abs

@Composable
fun InstructionsScreen(sourceUrl: String?, modifier: Modifier = Modifier) {
    key(sourceUrl) {
        val webView = remember { mutableStateOf<WebView?>(null) }
        val history = rememberSaveable(saver = Saver(
            save = { state -> Bundle().also { webView.value?.saveState(it) ?: it.putAll(state) } },
            restore = { it }
        )) { Bundle() }
        DisposableEffect(Unit) {
            onDispose {
                webView.value?.let { view ->
                    view.saveState(history)
                    view.destroy()
                }
            }
        }
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    coordinatePagerGestures()
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    if (history.isEmpty || restoreState(history) == null) sourceUrl?.let { loadUrl(it) }
                }.also { webView.value = it }
            }
        )
    }
}

private fun WebView.coordinatePagerGestures() {
    val slop = ViewConfiguration.get(context).scaledTouchSlop
    var downX = 0f
    var downY = 0f
    var directionLocked = false
    setOnTouchListener { view, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                directionLocked = false
            }
            MotionEvent.ACTION_MOVE -> if (!directionLocked) {
                val dx = abs(event.x - downX)
                val dy = abs(event.y - downY)
                if (maxOf(dx, dy) > slop) {
                    view.parent?.requestDisallowInterceptTouchEvent(dy >= dx)
                    directionLocked = true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                view.parent?.requestDisallowInterceptTouchEvent(false)
        }
        false
    }
}

@Preview(showBackground = true)
@Composable
private fun InstructionsPreview() {
    MyFoodyTheme { InstructionsScreen(sourceUrl = null) }
}
