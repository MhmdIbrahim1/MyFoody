package com.example.myfoody.util

import androidx.recyclerview.widget.DiffUtil

/**
 * RecipesDiffUtil class for calculating the differences between two lists of recipes.
 *
 * @property oldList The old list of Result objects.
 * @property newList The new list of Result objects.
 */

class RecipesDiffUtil<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    /**
     * Returns the size of the old list of recipes.
     *
     * @return The size of the old list.
     */
    override fun getOldListSize(): Int {
        return oldList.size
    }

    /**
     * Returns the size of the new list of recipes.
     *
     * @return The size of the new list.
     */
    override fun getNewListSize(): Int {
        return newList.size
    }

    /**
     * Determines if the items at the specified positions in the old and new lists are the same.
     *
     * @param oldItemPosition The position of the item in the old list.
     * @param newItemPosition The position of the item in the new list.
     * @return True if the items are the same, false otherwise.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    /**
     * Determines if the contents of the items at the specified positions in the old and new lists are the same.
     *
     * @param oldItemPosition The position of the item in the old list.
     * @param newItemPosition The position of the item in the new list.
     * @return True if the contents of the items are the same, false otherwise.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}