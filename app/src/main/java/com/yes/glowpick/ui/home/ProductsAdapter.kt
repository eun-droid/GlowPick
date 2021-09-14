package com.yes.glowpick.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yes.glowpick.R
import com.yes.glowpick.databinding.ListItemLoadingBinding
import com.yes.glowpick.databinding.ListItemProductBinding
import com.yes.glowpick.model.Product

class ProductsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var products: ArrayList<Product> = arrayListOf()
    private var _isShowLoading = false
    val isShowLoading: Boolean
        get() = _isShowLoading

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                ListItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_PRODUCT -> ProductViewHolder(
                ListItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ProductViewHolder(
                ListItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            holder.bind(products[position])
        } else {
            /* 로딩 아이템이므로 데이터 바인딩 필요 없음 */
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) {
            VIEW_TYPE_PRODUCT
        } else {
            VIEW_TYPE_LOADING
        }
    }

    override fun getItemCount(): Int {
        return if (_isShowLoading) {
            products.size + 1
        } else {
            products.size
        }
    }

    fun showLoading() {
        if (!_isShowLoading) {
            _isShowLoading = true
            notifyItemInserted(itemCount - 1)
            Log.i("productsAdapter", "show Loading")
        }
    }

    fun hideLoading() {
        if (_isShowLoading) {
            _isShowLoading = false
            notifyItemRemoved(itemCount)
            Log.i("productsAdapter", "hide Loading")
        }
    }

    fun addItems(addItemList:List<Product>) {
        Log.i("productsAdapter", "addItems")
        val positionStart = products.size
        products.addAll(addItemList.subList(positionStart, addItemList.size))

        Log.i("productsAdapter", "product item count ${products.count()}")
        notifyItemRangeInserted(positionStart, addItemList.size)
    }

    class ProductViewHolder(
        private val binding: ListItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener {
                binding.product?.let { product ->
                    navigateToProduct(product, it)
                }
            }
        }

        private fun navigateToProduct(
            product: Product,
            view: View
        ) {
            val direction =
                HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    product.imageUrl,
                    product.productTitle
                )
            view.findNavController().navigate(direction)
        }

        fun bind(item: Product) {
            binding.apply {
                product = item
                executePendingBindings()
            }
        }
    }

    class LoadingViewHolder(
        private val binding: ListItemLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_PRODUCT = 1
    }
}