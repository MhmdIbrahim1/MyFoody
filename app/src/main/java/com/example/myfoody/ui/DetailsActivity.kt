package com.example.myfoody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.myfoody.R
import com.example.myfoody.adapters.PagerAdapter
import com.example.myfoody.databinding.ActivityDetailsBinding
import com.example.myfoody.date.database.entities.FavoritesEntity
import com.example.myfoody.ui.fragments.ingredients.Ingredients
import com.example.myfoody.ui.fragments.instructions.Instructions
import com.example.myfoody.ui.fragments.overview.OverviewFragment
import com.example.myfoody.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity that displays the details of a recipe.
 */
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private var _binding: ActivityDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    private var recipeSaved = false
    private var savedRecipeId = 0
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Create a list of fragments to be displayed in the ViewPager
        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(Ingredients())
        fragments.add(Instructions())

        // Create a list of titles corresponding to each fragment
        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        // Pass the recipe details as a bundle to the PagerAdapter
        val resultBundle = Bundle()
        resultBundle.putParcelable("recipeBundle", args.result)

        // Create a PagerAdapter and set it to the ViewPager
        val pagerAdapter = PagerAdapter(resultBundle, fragments, this)

        binding.viewPager2.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    /**
     * Inflates the options menu.
     * @param menu The menu to be inflated.
     * @return Boolean value indicating if the menu was inflated.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.saved_favorite_menu)
        checkSavedRecipes(menuItem)
        return true
    }

    /**
     * Handles the selection of options menu items.
     * @param item The selected menu item.
     * @return Boolean value indicating if the item selection was handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.saved_favorite_menu && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.saved_favorite_menu && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Handles the selection of options menu items.
     */
    private fun checkSavedRecipes(menuItem: MenuItem) {
        // Observe the list of favorite recipes
        mainViewModel.readFavoriteRecipes.observe(this) { favoritesEntity ->
            try {
                // Loop through the list of favorite recipes
                for (savedRecipe in favoritesEntity) {
                    // If the recipe is already saved, change the color of the menu item
                    if (savedRecipe.result.id == args.result.id) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }

    /**
     * Saves the recipe to the favorites.
     * @param item The selected menu item.
     */
    private fun saveToFavorites(item: MenuItem) {
        // Create a FavoritesEntity object with the recipe details
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.result
            )
        // Add the recipe to the favorites
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        // Create a FavoritesEntity object with the recipe details
        val favoritesEntity =
            FavoritesEntity(
                savedRecipeId,
                args.result
            )
        // Remove the recipe from the favorites
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}.show()
    }

    /**
     * Changes the color of the menu item when it is selected.
     * @param item The selected menu item.
     * @param color The color to be set.
     */
    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

}


