package com.example.myfoody.ui.fragments.recipes.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfoody.R
import com.example.myfoody.date.MealAndDietType
import com.example.myfoody.ui.theme.MyFoodyTheme
import java.util.Locale

private data class FilterOption(@StringRes val label: Int, val id: Int)

private val meals = listOf(
    FilterOption(R.string.main_course, R.id.main_course_chip),
    FilterOption(R.string.side_dish, R.id.side_dish_chip),
    FilterOption(R.string.desert, R.id.dessert_chip),
    FilterOption(R.string.Appetizer, R.id.appetizer_chip),
    FilterOption(R.string.salad, R.id.salad_chip),
    FilterOption(R.string.bread, R.id.bread_chip),
    FilterOption(R.string.breakFast, R.id.breakfast_chip),
    FilterOption(R.string.soup, R.id.soup_chip),
    FilterOption(R.string.beverage, R.id.beverage_chip),
    FilterOption(R.string.sauce, R.id.sauce_chip),
    FilterOption(R.string.marinade, R.id.marinade_chip),
    FilterOption(R.string.finger_food, R.id.finger_food_chip),
    FilterOption(R.string.snack, R.id.snack_chip),
    FilterOption(R.string.drink, R.id.drink_chip)
)

private val diets = listOf(
    FilterOption(R.string.gluten_free, R.id.gluten_free_chip),
    FilterOption(R.string.ketogenic, R.id.ketogenic_chip),
    FilterOption(R.string.vegetarian, R.id.vegetarian_chip),
    FilterOption(R.string.vegan, R.id.vegan_chip),
    FilterOption(R.string.pescetarian, R.id.pescetarian_chip),
    FilterOption(R.string.paleo, R.id.paleo_chip),
    FilterOption(R.string.primal, R.id.primal_chip),
    FilterOption(R.string.whole30, R.id.whole30_chip)
)

@Composable
internal fun RecipeFiltersScreen(saved: MealAndDietType, applying: Boolean, onApply: (MealAndDietType) -> Unit) {
    // Persisted resource IDs can change between APKs. The stored query string identifies the option.
    var meal by rememberSaveable { mutableStateOf(saved.selectedMealType) }
    var diet by rememberSaveable { mutableStateOf(saved.selectedDietType) }
    val context = androidx.compose.ui.platform.LocalContext.current
    fun optionFor(options: List<FilterOption>, value: String): FilterOption =
        options.firstOrNull { context.getString(it.label).trim().equals(value.trim(), ignoreCase = true) }
            ?: options.first()

    val mealOption = optionFor(meals, meal)
    val dietOption = optionFor(diets, diet)
    Column(Modifier.fillMaxWidth().background(colorResource(R.color.cardBackgroundColor))
        .verticalScroll(rememberScrollState()).padding(vertical = 24.dp)) {
        FilterRow(R.string.meal_type, meals, mealOption) { meal = it }
        Spacer(Modifier.height(12.dp))
        FilterRow(R.string.diet_type, diets, dietOption) { diet = it }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                onApply(MealAndDietType(context.getString(mealOption.label).lowercase(Locale.ROOT), mealOption.id,
                    context.getString(dietOption.label).lowercase(Locale.ROOT), dietOption.id))
            },
            enabled = !applying,
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.buttonBackgroundColor)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp).height(48.dp)
        ) { Text(stringResource(R.string.apply), color = colorResource(R.color.white)) }
    }
}

@Composable
private fun FilterRow(@StringRes title: Int, options: List<FilterOption>, selected: FilterOption, onSelect: (String) -> Unit) {
    Text(stringResource(title), modifier = Modifier.padding(start = 24.dp), fontWeight = FontWeight.Bold,
        fontSize = 18.sp, color = colorResource(R.color.titleColor))
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val label = stringResource(option.label)
            val checked = option == selected
            val tint = colorResource(if (checked) R.color.checkedChipTextColor else R.color.chipTextColor)
            Box(Modifier.heightIn(min = 48.dp).selectable(selected = checked, role = Role.RadioButton,
                onClick = { onSelect(label.lowercase(Locale.ROOT)) }), contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(50), color = colorResource(
                    if (checked) R.color.checkedChipBackgroundColor else R.color.chipBackgroundColor)) {
                    Row(Modifier.heightIn(min = 32.dp).padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        if (checked) {
                            Icon(Icons.Default.Check, null, Modifier.size(18.dp), tint = tint)
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(label, fontSize = 14.sp, color = tint)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FiltersPreview() {
    MyFoodyTheme { RecipeFiltersScreen(MealAndDietType("main course", 0, "gluten free", 0), false) {} }
}
