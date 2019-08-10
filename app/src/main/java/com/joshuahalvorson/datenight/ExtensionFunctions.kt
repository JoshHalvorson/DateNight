package com.joshuahalvorson.datenight

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_random_restaurant.*


fun ImageView.loadImageWithPicasso(image: Int) {
    Picasso.get()
        .load(image)
        .noFade()
        .into(this)
}

fun ImageView.loadRatingImageWithPicasso(rating: Double) {
    when (rating) {
        0.0 -> this.loadImageWithPicasso(R.drawable.stars_small_0)
        1.0 -> this.loadImageWithPicasso(R.drawable.stars_small_1)
        1.5 -> this.loadImageWithPicasso(R.drawable.stars_small_1_half)
        2.0 -> this.loadImageWithPicasso(R.drawable.stars_small_2)
        2.5 -> this.loadImageWithPicasso(R.drawable.stars_small_2_half)
        3.0 -> this.loadImageWithPicasso(R.drawable.stars_small_3)
        3.5 -> this.loadImageWithPicasso(R.drawable.stars_small_3_half)
        4.0 -> this.loadImageWithPicasso(R.drawable.stars_small_4)
        4.5 -> this.loadImageWithPicasso(R.drawable.stars_small_4_half)
        5.0 -> this.loadImageWithPicasso(R.drawable.stars_small_5)
    }
}

fun View.animateViewWithYoYo(animation: Techniques, duration: Long, repeat: Int) {
    YoYo.with(animation)
        .duration(duration)
        .repeat(repeat)
        .playOn(this)
}

fun View.openUrlOnClick(url: String, context: Context) {
    this.setOnClickListener {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(context, intent, null)
    }
}