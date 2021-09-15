package com.yes.glowpick.ui.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.yes.glowpick.R
import com.yes.glowpick.model.ViewItem
import com.yes.glowpick.model.recommend.RecommendProduct
import com.yes.glowpick.util.Event
import java.text.DecimalFormat

/**
 * Contains [BindingAdapter]s for the [HomeFragment] list.
 */

@BindingAdapter("viewItems")
fun setViewItems(recyclerView: RecyclerView, viewItems: ArrayList<ViewItem>?) {
    if (viewItems != null) {
        val productsAdapter = recyclerView.adapter as ProductsAdapter
        val startPosition = productsAdapter.itemCount

        if (productsAdapter.addItems(viewItems)) {
            recyclerView.smoothScrollToPosition(startPosition)
        }
    }
}

@BindingAdapter("recommendItems")
fun setRecommendItems(recyclerView: RecyclerView, recommendItems: ArrayList<RecommendProduct>?) {
    if (recommendItems != null) {
        val recommendsAdapter = recyclerView.adapter as RecommendsAdapter
        recommendsAdapter.submitList(recommendItems)
    }
}

@BindingAdapter("showLoading")
fun showLoading(recyclerView: RecyclerView, dataLoading: Boolean) {
    val productsAdapter = recyclerView.adapter as ProductsAdapter
    if (dataLoading) {
        productsAdapter.showLoading()
    } else {
        productsAdapter.hideLoading()
    }
}

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_loading_icon)
            .error(R.drawable.ic_error_cloud_icon)
            .override(100)
            .into(imageView)
    }
}

@BindingAdapter("reviewCount")
fun setReviewCount(textView: TextView, reviewCountString: String) {
    val reviewCount = reviewCountString.toIntOrNull()
    reviewCount?.let {
        val df = DecimalFormat("#,###")
        val reviewText = "(리뷰 " + df.format(reviewCount) + ")"
        textView.text = reviewText
    }
}

@BindingAdapter("errorMessage")
fun setErrorMessage(textView: TextView, errorMessage: Event<String>?) {
    if (errorMessage != null) {
        errorMessage.getContentIfNotHandled()?.let {
            if (it.isEmpty()) {
                textView.setText(R.string.default_error_message)
            } else {
                textView.text = it
            }
        }
    }
}

@BindingAdapter("showSnackBar")
fun showSnackBar(view: View, snackBarMessage: Event<String>?) {
    if (snackBarMessage != null) {
        snackBarMessage.getContentIfNotHandled()?.let {
            if (it.isEmpty()) {
                Snackbar.make(view, R.string.default_error_message, LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, it, LENGTH_SHORT).show()
            }
        }
    }
}