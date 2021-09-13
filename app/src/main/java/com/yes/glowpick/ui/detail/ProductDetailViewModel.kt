package com.yes.glowpick.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductDetailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Product detail Fragment"
    }
    val text: LiveData<String> = _text
}