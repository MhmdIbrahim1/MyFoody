package com.example.myfoody.ui.fragments.foodJoke

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.myfoody.R
import com.example.myfoody.ui.theme.composeContent
import com.example.myfoody.util.Constants.Companion.API_KEY
import com.example.myfoody.util.NetworkResult
import com.example.myfoody.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {
    private val mainViewModel by viewModels<MainViewModel>()
    private var shareText = "No Food Joke"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        composeContent {
            val response by mainViewModel.foodJokeResponse.observeAsState()
            val cached by mainViewModel.readFoodJoke.observeAsState()
            val joke = when (response) {
                is NetworkResult.Success -> response?.data?.text
                is NetworkResult.Error -> cached?.firstOrNull()?.foodJoke?.text
                else -> null
            }
            SideEffect { if (joke != null) shareText = joke }
            FoodJokeScreen(
                joke = joke,
                loading = response is NetworkResult.Loading,
                error = response?.message?.takeIf { response is NetworkResult.Error && cached?.isEmpty() == true }
            )
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId != R.id.share_food_joke_menu) return false
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                })
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
            if (response is NetworkResult.Error) {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
