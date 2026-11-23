package com.example.myfoody.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myfoody.ui.theme.composeContent
import com.example.myfoody.viewModels.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class RecipesBottomSheet : BottomSheetDialogFragment() {
    private val recipesViewModel: RecipesViewModel by activityViewModels()
    private val savedFilters by lazy { recipesViewModel.readMealAndDietType.asLiveData() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        composeContent {
            val saved by savedFilters.observeAsState()
            var applying by remember { mutableStateOf(false) }
            saved?.let {
                RecipeFiltersScreen(it, applying) { chosen ->
                    if (!applying) {
                        applying = true
                        viewLifecycleOwner.lifecycleScope.launch {
                            recipesViewModel.saveMealAndDietType(
                                chosen.selectedMealType, chosen.selectedMealTypeId,
                                chosen.selectedDietType, chosen.selectedDietTypeId
                            ).join()
                            findNavController().navigate(
                                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
                            )
                        }
                    }
                }
            }
        }
}
