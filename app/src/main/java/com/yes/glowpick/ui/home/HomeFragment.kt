package com.yes.glowpick.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.yes.glowpick.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val homeViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private lateinit var binding: FragmentHomeBinding
    private var listPositionState = RecyclerView.NO_POSITION    // 복원을 위한 리스트 위치값 상태 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)

        with(binding) {
            viewModel = homeViewModel
            lifecycleOwner = this@HomeFragment

            with(productList) {
                adapter = ProductsAdapter()

                addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

                addOnScrollListener(object: RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0) { // scroll down
                            val lastVisibleItemPosition =
                                (productList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                            val endItemPosition = productList.adapter?.itemCount?.minus(1)

                            if (lastVisibleItemPosition == endItemPosition) {
                                val productsAdapter = recyclerView.adapter as ProductsAdapter
                                if (!productsAdapter.isShowLoading) {
                                    homeViewModel.moreProductList()
                                }
                            }
                        }
                    }
                })
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (listPositionState != RecyclerView.NO_POSITION) {
            binding.productList.scrollToPosition(listPositionState)
        }
    }

    override fun onPause() {
        super.onPause()

        listPositionState = (binding.productList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    }
}