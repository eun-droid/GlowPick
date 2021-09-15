package com.yes.glowpick.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.yes.glowpick.R
import com.yes.glowpick.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        with(binding) {
            // product image setting
            Glide.with(root.context)
                .load(args.imageUrl)
                .placeholder(R.drawable.ic_loading_icon)
                .error(R.drawable.ic_error_cloud_icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(productDetailImage)

            // product name setting
            productDetailName.text = args.name
        }

        return root
    }
}