package com.joshuahalvorson.datenight

import android.view.View
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Picasso


fun ImageView.loadImageWithPicasso(image: Int) {
    Picasso.get()
        .load(image)
        .noFade()
        .into(this)
}

fun View.animateViewWithYoYo(animation: Techniques, duration: Long, repeat: Int) {
    YoYo.with(animation)
        .duration(duration)
        .repeat(repeat)
        .playOn(this)
}