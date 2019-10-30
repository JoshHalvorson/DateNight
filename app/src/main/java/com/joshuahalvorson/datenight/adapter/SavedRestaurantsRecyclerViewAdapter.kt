package com.joshuahalvorson.datenight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.util.loadRatingImageWithPicasso
import com.joshuahalvorson.datenight.model.SavedRestaurant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.saved_restaurant_list_item.view.*

class SavedRestaurantsRecyclerViewAdapter(
    private val restaurants: ArrayList<SavedRestaurant>,
    private val callback: OnListItemClick
) : RecyclerView.Adapter<SavedRestaurantsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.saved_restaurant_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindModel(restaurants[position], callback)
    }

    override fun getItemCount() = restaurants.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val restaurantName: TextView = itemView.saved_restaurant_name
        private val restaurantPrice: TextView = itemView.saved_restaurant_price
        private val restaurantRating: ImageView = itemView.saved_restaurant_rating
        private val restaurantImage: ImageView = itemView.saved_restaurant_image

        fun bindModel(restaurant: SavedRestaurant, callback: OnListItemClick) {
            itemView.setOnClickListener { callback.onListItemClick(restaurant) }
            restaurantName.text = restaurant.name
            restaurantPrice.text = restaurant.price
            restaurant.rating?.let { restaurantRating.loadRatingImageWithPicasso(it) }
            Picasso.get()
                .load(restaurant.image_url)
                .resize(500, 500)
                .centerCrop()
                .into(restaurantImage)
        }
    }

    interface OnListItemClick {
        fun onListItemClick(restaurant: SavedRestaurant?)
    }
}