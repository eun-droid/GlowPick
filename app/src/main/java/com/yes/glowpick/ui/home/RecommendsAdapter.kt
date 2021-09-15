package com.yes.glowpick.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yes.glowpick.databinding.ListItemRecommendBinding
import com.yes.glowpick.model.recommend.RecommendProduct

/**
 * Adapter for Recommend Product RecyclerView
 */
class RecommendsAdapter: ListAdapter<RecommendProduct, RecyclerView.ViewHolder>(RecommendProductDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecommendViewHolder(
            ListItemRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecommendViewHolder).bind(getItem(position))
    }

    override fun submitList(list: MutableList<RecommendProduct>?) {
        list?.let {
            super.submitList(ArrayList(list))
        }
    }

    class RecommendViewHolder(
        private val binding: ListItemRecommendBinding
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                binding.recommendProduct?.let { recommendProduct ->
                    navigateToRecommendProduct(recommendProduct, it)
                }
            }
        }

        private fun navigateToRecommendProduct(
            recommendProduct: RecommendProduct,
            view: View
        ) {
            val direction =
                HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    recommendProduct.imageUrl,
                    recommendProduct.productTitle
                )
            view.findNavController().navigate(direction)
        }

        fun bind(item: RecommendProduct) {
            binding.apply {
                recommendProduct = item
                executePendingBindings()
            }
        }
    }
}

private class RecommendProductDiffCallback : DiffUtil.ItemCallback<RecommendProduct>() {
    override fun areItemsTheSame(oldItem: RecommendProduct, newItem: RecommendProduct): Boolean {
        return oldItem.idProduct == newItem.idProduct
    }

    override fun areContentsTheSame(oldItem: RecommendProduct, newItem: RecommendProduct): Boolean {
        return oldItem == newItem
    }

}