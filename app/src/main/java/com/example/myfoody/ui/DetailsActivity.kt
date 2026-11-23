package com.example.myfoody.ui

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.navArgs
import com.example.myfoody.R
import com.example.myfoody.date.database.entities.FavoritesEntity

import com.example.myfoody.ui.fragments.ingredients.IngredientsScreen
import com.example.myfoody.ui.fragments.instructions.InstructionsScreen
import com.example.myfoody.ui.fragments.overview.OverviewScreen
import com.example.myfoody.ui.theme.MyFoodyTheme
import kotlinx.coroutines.launch
import com.example.myfoody.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = ComposeView(this).apply { id = R.id.details_content }
        setContentView(root)
        root.setContent {
            MyFoodyTheme {
                val favorites by mainViewModel.readFavoriteRecipes.observeAsState()
                val saved = favorites?.firstOrNull { it.result.id == args.result.id }
                var pendingFavorite by remember { mutableStateOf<Boolean?>(null) }
                LaunchedEffect(saved, pendingFavorite) {
                    if (pendingFavorite == (saved != null)) pendingFavorite = null
                }
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
                                Icon(Icons.Default.ArrowBack, stringResource(androidx.appcompat.R.string.abc_action_bar_up_description))
                            }
                        },
                        actions = {
                            IconButton(enabled = favorites != null && pendingFavorite == null, onClick = {
                                if (pendingFavorite != null) return@IconButton
                                pendingFavorite = saved == null
                                val message = if (saved == null) {
                                    mainViewModel.insertFavoriteRecipe(FavoritesEntity(0, args.result))
                                    "Recipe saved."
                                } else {
                                    mainViewModel.deleteFavoriteRecipe(saved)
                                    "Removed from Favorites."
                                }
                                Snackbar.make(root, message, Snackbar.LENGTH_SHORT).setAction("Okay") {}.show()
                            }) {
                                Icon(painterResource(R.drawable.ic_star), stringResource(R.string.saved_favorite_menu),
                                    tint = if (saved == null) Color.White else colorResource(R.color.yellow))
                            }
                        }
                    )
                    TabRow(selectedTabIndex = pager.currentPage, backgroundColor = background, contentColor = Color.White) {
                        tabs.forEachIndexed { index, label ->
                            Tab(selected = pager.currentPage == index,
                                onClick = { scope.launch { pager.animateScrollToPage(index) } },
                                modifier = Modifier.height(48.dp)) {
                                Text(label.uppercase(), maxLines = 1, style = MaterialTheme.typography.button,
                                    letterSpacing = 0.sp, color = Color.White)
                            }
                        }
                    }
                    HorizontalPager(pageCount = tabs.size, state = pager, modifier = Modifier.weight(1f)) { page ->
                        when (page) {
                            0 -> OverviewScreen(args.result)
                            1 -> IngredientsScreen(args.result.extendedIngredients)
                            2 -> InstructionsScreen(args.result.sourceUrl)
                        }
                    }
                }
            }
        }
    }
}
