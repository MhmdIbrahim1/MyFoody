package com.example.myfoody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myfoody.R
import com.example.myfoody.databinding.IngredientsRowLayoutBinding
import com.example.myfoody.models.ExtendedIngredient
import com.example.myfoody.util.Constants.Companion.BASE_IMAGE_URL
import com.example.myfoody.util.RecipesDiffUtil
import java.util.*

/**
 * Adapter class for the Ingredients RecyclerView.
 */
class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    private var ingredientsList = emptyList<ExtendedIngredient>()

    /**
     * ViewHolder class for the IngredientsAdapter.
     */
    class MyViewHolder(val binding: IngredientsRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the item layout for the ViewHolder
        return MyViewHolder(
            IngredientsRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind the data with the actual views
        val ingredient = ingredientsList[position]

        holder.binding.ingredientImageView.load(BASE_IMAGE_URL + ingredient.image) {
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        holder.binding.ingredientName.text = ingredient.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        holder.binding.ingredientAmount.text = ingredient.amount.toString()
        holder.binding.ingredientUnit.text = ingredient.unit
        holder.binding.ingredientConsistency.text = ingredient.consistency
        holder.binding.ingredientOriginal.text = ingredient.original
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    /**
     * Update the data of the adapter with new ingredients list.
     * @param newIngredients The new list of ingredients.
     */
    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil = RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }
}
