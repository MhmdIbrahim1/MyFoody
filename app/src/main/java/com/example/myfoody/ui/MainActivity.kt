package com.example.myfoody.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myfoody.R
import com.example.myfoody.ui.theme.MyFoodyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val container = FragmentContainerView(this).apply { id = R.id.navHostFragment }
        val navigation = ComposeView(this)
        // Attach the container before restoring or creating the NavHost's fragment views.
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(container, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(navigation, LinearLayout.LayoutParams(-1, -2))
        })
        val manager = supportFragmentManager
        val host = manager.findFragmentById(container.id) as? NavHostFragment
            ?: NavHostFragment.create(R.navigation.my_nav).also {
                manager.beginTransaction().add(container.id, it).setPrimaryNavigationFragment(it).commitNow()
            }
        navController = host.navController
        val menu = PopupMenu(this, navigation).also { it.inflate(R.menu.bottom_nav_menu) }.menu
        val items = List(menu.size()) { menu.getItem(it) }
        setupActionBarWithNavController(navController, AppBarConfiguration(items.map { it.itemId }.toSet()))
        navigation.setContent {
            MyFoodyTheme {
                var selected by remember { mutableStateOf(navController.currentDestination?.id) }
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
                        selected = if (destination is FloatingWindow) controller.previousBackStackEntry?.destination?.id else destination.id
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose { navController.removeOnDestinationChangedListener(listener) }
                }
                BottomNavigation(backgroundColor = colorResource(R.color.bottomNavBackground), modifier = Modifier.height(56.dp)) {
                    items.forEach { item ->
                        val icon = when (item.itemId) {
                            R.id.recipesFragment -> R.drawable.ic_menu_book
                            R.id.favoriteRecipesFragment -> R.drawable.ic_star
                            else -> R.drawable.ic_food_joke
                        }
                        BottomNavigationItem(
                            selected = selected == item.itemId,
                            onClick = { NavigationUI.onNavDestinationSelected(item, navController) },
                            icon = { Icon(painterResource(icon), null, Modifier.size(24.dp)) },
                            label = { Text(item.title.toString()) },
                            selectedContentColor = colorResource(R.color.bottomNavCheckedItemColor),
                            unselectedContentColor = colorResource(R.color.bottomNavItemColor)
                        )
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}
