package com.joshuahalvorson.datenight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.util.loadRatingImageWithPicasso
import com.joshuahalvorson.datenight.model.Review
import com.joshuahalvorson.datenight.util.openUrlOnClick
import kotlinx.android.synthetic.main.restaurant_reviews_list_item.view.*

class RestaurantReviewsRecyclerViewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<RestaurantReviewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.restaurant_reviews_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindModel(reviews[position])
    }

    override fun getItemCount() = reviews.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reviewUserName: TextView = itemView.review_user_name
        private val reviewTimestamp: TextView = itemView.review_time_stamp
        private val reviewText: TextView = itemView.review_text
        private val reviewRating: ImageView = itemView.review_rating
        private val reviewYelpImage: ImageView = itemView.review_open_imageview
        private val context = view.context

        fun bindModel(review: Review) {
            reviewUserName.text = review.user?.name
            reviewTimestamp.text = review.time_created
            reviewText.text = review.text
            review.rating?.let { reviewRating.loadRatingImageWithPicasso(it) }
            review.url?.let { reviewYelpImage.openUrlOnClick(it, context) }
        }
    }
}