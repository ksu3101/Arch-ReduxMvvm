package com.example.mvvm.arch_reduxmvvm.base.databinding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @author beemo
 * @since 2020-03-04
 */

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visibility: Boolean) {
    view.visibility = if (visibility) View.VISIBLE else View.GONE
}

@BindingAdapter("imageUrl")
fun loadImageUrl(iv: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        iv.setImageDrawable(null)
    } else {
        Glide.with(iv.context)
            .load(url)
            .into(iv)
    }
}

@BindingAdapter("circleImageLoad")
fun loadImageUrlAndCircle(iv: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        iv.setImageDrawable(null)
    } else {
        Glide.with(iv.context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .into(iv)
    }
}