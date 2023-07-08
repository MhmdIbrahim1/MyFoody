package com.example.myfoody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfoody.R
import com.example.myfoody.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // This annotation is used to tell the compiler that this is the application class
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding // binding object
   private lateinit var navController: NavController // navigation controller
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater) // inflate the layout
        setContentView(binding.root) // set the content view to the root of the binding object

        // set up the navigation controller
        navController = findNavController(R.id.navHostFragment)
        val appBarConfiguration = AppBarConfiguration(setOf( // pass the destination id's
            R.id.recipesFragment,
            R.id.favoriteRecipesFragment,
            R.id.foodJokeFragment
        ))

        binding.bottomNavigationView.setupWithNavController(navController) // set up the bottom navigation view
        setupActionBarWithNavController(navController, appBarConfiguration) // set up the action bar with the navigation controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp() // navigate up or super navigate up
    }

}