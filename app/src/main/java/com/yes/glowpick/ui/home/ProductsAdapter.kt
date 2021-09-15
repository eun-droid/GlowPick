package com.yes.glowpick.ui.home

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yes.glowpick.databinding.ListItemLoadingBinding
import com.yes.glowpick.databinding.ListItemProductBinding
import com.yes.glowpick.databinding.ListItemRecommendsBinding
import com.yes.glowpick.model.RecommendsViewItem
import com.yes.glowpick.model.ViewItem
import com.yes.glowpick.model.ViewType
import com.yes.glowpick.model.product.Product
import com.yes.glowpick.util.HorizontalItemDecorator
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList

/**
 * Adapter for Product RecyclerView
 */
class ProductsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewItem> = arrayListOf()
    private val layoutManagerStates = hashMapOf<Int, Parcelable?>()         // 중첩 리사이클러뷰 layoutManager state 보관
    private val visibleScrollableViews = hashMapOf<Int, ViewHolderRef>()    // 중첩 리사이클러뷰 ViewHolder reference 보관

    private var _isShowLoading = false
    val isShowLoading: Boolean
        get() = _isShowLoading

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is RecommendsViewHolder) {
            val state = holder.getLayoutManager()?.onSaveInstanceState()
            layoutManagerStates[holder.getId()] = state

            visibleScrollableViews.remove(holder.hashCode())
        }

        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ViewType.PRODUCT.ordinal -> ProductViewHolder(
                ListItemProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ViewType.RECOMMENDS.ordinal -> RecommendsViewHolder(
                ListItemRecommendsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ViewType.LOADING.ordinal -> LoadingViewHolder(
                ListItemLoadingBinding.inflate(
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
        val viewData = items[position].any

        if (holder is ProductViewHolder) {
            if (viewData is Product) {
                holder.bind(viewData)
            }
        }
        else if (holder is RecommendsViewHolder) {
            if (viewData is RecommendsViewItem) {
                holder.bind(viewData)
            }

            holder.getLayoutManager()?.let {
                val state: Parcelable? = layoutManagerStates[holder.getId()]
                if (state != null) {
                    it.onRestoreInstanceState(state)
                } else {
                    it.scrollToPosition(0)
                }
            }
            visibleScrollableViews[holder.hashCode()] = ViewHolderRef(
                holder.getId(),
                WeakReference(holder)
            )
        }
        else {
            /* 로딩 아이템이므로 데이터 바인딩 필요 없음 */
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Loading view를 아이템리스트에 추가해 보여준다
     */
    fun showLoading() {
        if (!_isShowLoading) {
            _isShowLoading = true
            items.add(ViewItem(viewType = ViewType.LOADING))
            notifyItemInserted(itemCount - 1)
        }
    }

    /**
     * Loading view를 아이템리스트에서 제거해 숨긴다
     */
    fun hideLoading() {
        if (_isShowLoading) {
            _isShowLoading = false
            items.removeAt(items.lastIndex)
            notifyItemRemoved(itemCount)
        }
    }

    fun addItems(viewItems: List<ViewItem>): Boolean {
        var added = false

        val positionStart = items.size
        val viewItemSize = viewItems.size

        val addList = viewItems.subList(positionStart, viewItemSize)
        if (addList.isNotEmpty()) {
            saveState() // 아이템을 추가할 때 중첩 리사이클러뷰의 상태를 저장

            items.addAll(addList)
            notifyItemRangeInserted(positionStart, viewItemSize)

            added = true
        }

        return added
    }

    /**
     * 따로 보관해둔 중첩 리사이클러뷰 reference를 참고해 layoutManager State를 저장한다
     */
    fun saveState() {
        visibleScrollableViews.values.forEach { item ->
            item.reference.get()?.let {
                layoutManagerStates[item.id] = it.getLayoutManager()?.onSaveInstanceState()
            }
        }
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

    class RecommendsViewHolder(
        private val binding: ListItemRecommendsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: RecommendsViewItem

        init {
            with(binding.recommendList) {
                adapter = RecommendsAdapter()
                layoutManager = object: LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false) {
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                        lp?.width = (width * 0.4).toInt()
                        return true
                    }
                }

                addItemDecoration(HorizontalItemDecorator(12))
            }
        }

        fun getLayoutManager(): RecyclerView.LayoutManager? {
            return binding.recommendList.layoutManager
        }

        fun getId() = item.index

        fun bind(item: RecommendsViewItem) {
            binding.apply {

                this@RecommendsViewHolder.item = item
                viewItem = item
                executePendingBindings()
            }
        }
    }

    class LoadingViewHolder(
        private val binding: ListItemLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    private data class ViewHolderRef(
        val id: Int,
        val reference: WeakReference<RecommendsViewHolder>
    )
}