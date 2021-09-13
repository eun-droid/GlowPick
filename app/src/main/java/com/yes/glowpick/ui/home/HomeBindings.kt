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
import com.yes.glowpick.model.Product
import java.text.DecimalFormat

/**
 * Contains [BindingAdapter]s for the [HomeFragment] list.
 */

@BindingAdapter("productItems")
fun setProductItems(recyclerView: RecyclerView, items: ArrayList<Product>?) {
    if (items != null) {
        val productsAdapter = recyclerView.adapter as ProductsAdapter

        productsAdapter.hideLoading()

        val startPosition = productsAdapter.itemCount
        productsAdapter.addItems(items)
        recyclerView.smoothScrollToPosition(startPosition)
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
fun setErrorMessage(textView: TextView, errorMessage: String?) {
    if (errorMessage != null) {
        if (errorMessage.isEmpty()) {
            textView.setText(R.string.default_error_message)
        } else {
            textView.text = errorMessage
        }
    }
}

@BindingAdapter("showSnackBar")
fun showSnackBar(view: View, snackBarMessage: String?) {
    if (snackBarMessage != null) {
        if (snackBarMessage.isEmpty()) {
            Snackbar.make(view, R.string.default_error_message, LENGTH_SHORT).show()
        } else {
            Snackbar.make(view, snackBarMessage, LENGTH_SHORT).show()
        }
    }
}