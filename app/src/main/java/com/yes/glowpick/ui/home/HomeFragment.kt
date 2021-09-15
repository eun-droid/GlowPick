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
    private var productsAdapter: ProductsAdapter? = null

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

                if (productsAdapter != null) {
                    adapter = productsAdapter

                } else {
                    productsAdapter = ProductsAdapter()

                    adapter = productsAdapter
                }

                addItemDecoration(DividerItemDecoration(this.context, VERTICAL))

                addOnScrollListener(object: RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0) { // scroll down
                            val lastVisibleItemPosition =
                                (productList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                            val endItemPosition = productList.adapter?.itemCount?.minus(1)

                            // 스크롤이 리스트 마지막 아이템까지 도달하면
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

    override fun onStop() {
        super.onStop()
        productsAdapter?.saveState()    // 중첩 리사이클러뷰 상태 저장
    }
}