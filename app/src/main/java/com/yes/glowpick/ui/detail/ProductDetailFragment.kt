package com.yes.glowpick.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yes.glowpick.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private lateinit var productDetailViewModel: ProductDetailViewModel
    private var _binding: FragmentProductDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productDetailViewModel =
            ViewModelProvider(this).get(ProductDetailViewModel::class.java)

        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProductDetail
        productDetailViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}