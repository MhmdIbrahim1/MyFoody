package com.example.myfoody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfoody.databinding.RecipesRowLayoutBinding
import com.example.myfoody.models.FoodRecipe
import com.example.myfoody.models.Result
import com.example.myfoody.util.RecipesDiffUtil

/**
 * RecipesAdapter class for handling the display of a list of recipes in a RecyclerView.
 */
class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    // A list of Result objects representing the recipes.
    private var recipes = emptyList<Result>()

    /**
     * MyViewHolder class that inflates and binds the recipe data to the view.
     *
     * @property binding The binding object for the recipe row layout.
     */
    class MyViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the given Result object to the view.
         *
         * @param result The Result object representing a recipe.
         */
        fun bind(result: Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            /**
             * A factory method to create an instance of MyViewHolder.
             *
             * @param parent The parent ViewGroup that the ViewHolder will be attached to.
             * @return A new instance of MyViewHolder.
             */
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    /**
     * Sets the adapter's data to the given FoodRecipe object and notifies the adapter of data changes.
     *
     * @param newData A FoodRecipe object containing the new data.
     */
    fun setData(newData: FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}