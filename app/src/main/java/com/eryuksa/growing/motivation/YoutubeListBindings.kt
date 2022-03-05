package com.eryuksa.growing.motivation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.eryuksa.growing.R

@BindingAdapter("app:thumbnail")
fun setThumbnail(imageView: ImageView, thumbnailUrl: String) {
    Glide.with(imageView.context)
        .load(thumbnailUrl)
        .error(R.drawable.video_24_round)
        .fallback(R.drawable.video_24_round)
        .transform(RoundedCorners(12))
        .into(imageView)
}