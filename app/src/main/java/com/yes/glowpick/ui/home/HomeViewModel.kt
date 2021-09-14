package com.yes.glowpick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yes.glowpick.GlowPickAPI
import com.yes.glowpick.model.Product
import com.yes.glowpick.model.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _products = MutableLiveData<ArrayList<Product>>()
    val products: LiveData<ArrayList<Product>>
        get() = _products

    private val _dataLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackBarMessage = MutableLiveData<String>()
    val snackBarMessage: LiveData<String>
        get() = _snackBarMessage

    private val _frontMessage = MutableLiveData<String>()
    val frontMessage: LiveData<String>
        get() = _frontMessage

    private val _occurLoadError = MutableLiveData<Boolean>()
    val occurLoadError: LiveData<Boolean>
        get() = _occurLoadError

    private val glowPickAPI = GlowPickAPI.create()
    private var currentPageNumber = 1   // 제품 페이지 번호는 1부터 시작

    init {
        getProductList()
    }

    fun getProductList() {
        glowPickAPI.getProducts(currentPageNumber).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                _dataLoading.value = false

                if (response.isSuccessful) {
                    response.body()?.products?.let {
                        if (_products.value == null) {
                            _products.value = it
                        } else {
                            _products.value?.let { existingList ->
                                existingList.addAll(it)
                                _products.value = existingList
                            }
                        }
                    }
                    currentPageNumber++
                } else {
                    error(response.message() ?: "")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _dataLoading.value = false
                error(t.message ?: "")
            }
        })
    }

    fun moreProductList() {
        _dataLoading.value = true
        getProductList()
    }

    fun clickRetryButton() {
        _occurLoadError.value = false
        getProductList()
    }

    fun error(errorMessage: String) {
        if (currentPageNumber == 1) { // 초기화 실패한 경우 전면 오류메시지 화면 표시
            _frontMessage.value = errorMessage
            _occurLoadError.value = true
        } else {    // 그외 스낵바 표시
            _snackBarMessage.value = errorMessage
        }
    }
}