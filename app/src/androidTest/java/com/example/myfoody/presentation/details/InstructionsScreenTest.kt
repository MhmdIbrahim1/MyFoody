package com.example.myfoody.presentation.details

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.core.view.descendants
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class InstructionsScreenTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private fun currentWebView() =
        (compose.activity.window.decorView as ViewGroup).descendants.filterIsInstance<WebView>().single()

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun webButtonAndVerticalDragStayInInstructionsAndHorizontalDragChangesPage() {
        lateinit var pager: PagerState
        compose.setContent {
            pager = rememberPagerState(initialPage = 1)
            HorizontalPager(pageCount = 2, state = pager, modifier = Modifier.fillMaxSize()) { page ->
                if (page == 1) InstructionsScreen(null) else Spacer(Modifier.fillMaxSize())
            }
        }
        val view = compose.runOnIdle { currentWebView() }
        val html = """
            <!doctype html><meta name="viewport" content="width=device-width, initial-scale=1">
            <body style="margin:0"><button style="width:100%;height:200px"
                onclick="document.title='accepted'">Accept All</button><div style="height:8000px"></div></body>
        """.trimIndent()
        compose.runOnUiThread { view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null) }
        compose.waitUntil(10_000) {
            compose.runOnUiThread { view.progress == 100 && view.contentHeight > 1000 }
        }
        touch(view, 0.5f, 0.05f)
        compose.waitUntil(10_000) { compose.runOnUiThread { view.title == "accepted" } }
        touch(view, 0.25f, 0.8f, 0.65f, 0.3f)
        compose.waitForIdle()
        assertEquals(1, compose.runOnIdle { pager.currentPage })
        compose.waitUntil(10_000) { compose.runOnUiThread { view.scrollY > 0 } }
        touch(view, 0.1f, 0.5f, 0.95f, 0.5f)
        compose.waitForIdle()
        assertEquals(0, compose.runOnIdle { pager.currentPage })
    }

    private fun touch(view: WebView, x: Float, y: Float, toX: Float = x, toY: Float = y) {
        val location = IntArray(2)
        val (width, height) = compose.runOnUiThread {
            view.getLocationOnScreen(location)
            view.width to view.height
        }
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val down = SystemClock.uptimeMillis()
        fun send(action: Int, fraction: Float) {
            val event = MotionEvent.obtain(
                down,
                SystemClock.uptimeMillis(),
                action,
                location[0] + width * (x + (toX - x) * fraction),
                location[1] + height * (y + (toY - y) * fraction),
                0
            )
            event.source = InputDevice.SOURCE_TOUCHSCREEN
            try {
                assertTrue(automation.injectInputEvent(event, true))
            } finally {
                event.recycle()
            }
        }
        send(MotionEvent.ACTION_DOWN, 0f)
        if (x != toX || y != toY) {
            for (step in 1..20) {
                SystemClock.sleep(16)
                send(MotionEvent.ACTION_MOVE, step / 20f)
            }
        }
        send(MotionEvent.ACTION_UP, 1f)
    }

    @Test
    fun recompositionPreservesThePageOpenedInsideTheWebView() {
        val sourceUrl = "about:blank#recipe"
        val nextUrl = "about:blank#step-two"
        val padding = mutableStateOf(0.dp)
        compose.setContent {
            InstructionsScreen(sourceUrl, Modifier.padding(padding.value))
        }

        val webView = compose.runOnIdle {
            currentWebView().also {
                assertTrue(it.settings.domStorageEnabled)
                assertEquals(WebSettings.LOAD_DEFAULT, it.settings.cacheMode)
            }
        }
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.runOnUiThread { webView.url == sourceUrl && webView.progress == 100 }
        }
        compose.runOnUiThread { webView.evaluateJavascript("location.hash = 'step-two'", null) }
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.runOnUiThread { webView.url == nextUrl }
        }

        compose.runOnIdle { padding.value = 8.dp }
        compose.runOnIdle {
            assertSame(webView, currentWebView())
            assertEquals(nextUrl, webView.url)
        }
    }
}
