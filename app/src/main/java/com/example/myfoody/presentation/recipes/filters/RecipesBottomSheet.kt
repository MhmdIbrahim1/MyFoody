package com.example.myfoody.presentation.recipes.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myfoody.presentation.theme.composeContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment() {
    private val viewModel by viewModels<RecipeFiltersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = composeContent {
        val uiState by viewModel.uiState.collectAsState()
        uiState.savedFilter?.let { saved ->
            RecipeFiltersScreen(
                saved = saved,
                applying = uiState.isApplying,
                onApply = viewModel::onApplyFilters
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        RecipeFiltersUiEvent.FiltersApplied -> {
                            findNavController().navigate(
                                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
                            )
                        }
                    }
                }
            }
        }
    }
}
