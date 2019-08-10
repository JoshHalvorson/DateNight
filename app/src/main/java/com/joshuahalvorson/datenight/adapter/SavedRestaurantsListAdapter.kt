package com.joshuahalvorson.datenight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.loadRatingImageWithPicasso
import com.joshuahalvorson.datenight.model.Review
import com.joshuahalvorson.datenight.openUrlOnClick
import kotlinx.android.synthetic.main.restaurant_reviews_list_item.view.*
import kotlinx.android.synthetic.main.saved_restaurant_list_item.view.*

class SavedRestaurantsListAdapter(private val restaurants: List<Pair<String, String>>) : RecyclerView.Adapter<SavedRestaurantsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.saved_restaurant_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindModel(restaurants[position])
    }

    override fun getItemCount() = restaurants.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val restaurantName: TextView = itemView.saved_restaurant_name

        fun bindModel(restaurant: Pair<String, String>) {
            restaurantName.text = restaurant.second
        }
    }
}