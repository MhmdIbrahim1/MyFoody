package com.example.myfoody.presentation.details

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.example.myfoody.R
import com.example.myfoody.presentation.navigation.toDomain
import com.example.myfoody.presentation.theme.MyFoodyTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private val args by navArgs<DetailsActivityArgs>()
    private val viewModel: DetailsViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recipe = args.result.toDomain()
        viewModel.setRecipe(recipe)

        val root = ComposeView(this).apply { id = R.id.details_content }
        setContentView(root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is DetailsUiEvent.ShowSnackbar -> {
                            Snackbar.make(root, event.message, Snackbar.LENGTH_SHORT)
                                .setAction("Okay") {}
                                .show()
                        }
                    }
                }
            }
        }

        root.setContent {
            MyFoodyTheme {
                val uiState by viewModel.uiState.collectAsState()
                val activeRecipe = uiState.recipe ?: recipe
                val pager = rememberPagerState()
                val scope = rememberCoroutineScope()
                val tabs = listOf("Overview", "Ingredients", "Instructions")
                val background = colorResource(R.color.actionBarBackgroundColor)
                Column(Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text(title.toString()) },
                        backgroundColor = background,
                        contentColor = Color.White,
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    stringResource(androidx.appcompat.R.string.abc_action_bar_up_description)
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = uiState.isFavoriteActionEnabled,
                                onClick = viewModel::onToggleFavoriteClicked
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_star),
                                    stringResource(R.string.saved_favorite_menu),
                                    tint = if (uiState.isFavorite) colorResource(R.color.yellow) else Color.White
                                )
                            }
                        }
                    )
                    TabRow(
                        selectedTabIndex = pager.currentPage,
                        backgroundColor = background,
                        contentColor = Color.White
                    ) {
                        tabs.forEachIndexed { index, label ->
                            Tab(
                                selected = pager.currentPage == index,
                                onClick = { scope.launch { pager.animateScrollToPage(index) } },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    maxLines = 1,
                                    style = MaterialTheme.typography.button,
                                    letterSpacing = 0.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    HorizontalPager(pageCount = tabs.size, state = pager, modifier = Modifier.weight(1f)) { page ->
                        when (page) {
                            0 -> OverviewScreen(activeRecipe)
                            1 -> IngredientsScreen(activeRecipe.extendedIngredients)
                            2 -> InstructionsScreen(activeRecipe.sourceUrl)
                        }
                    }
                }
            }
        }
    }
}
