package com.yes.glowpick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.LinkedTreeMap
import com.yes.glowpick.GlowPickAPI
import com.yes.glowpick.model.RecommendsViewItem
import com.yes.glowpick.model.ViewItem
import com.yes.glowpick.model.ViewType
import com.yes.glowpick.model.product.Product
import com.yes.glowpick.model.product.ProductResponse
import com.yes.glowpick.model.recommend.RecommendProduct
import com.yes.glowpick.util.Event
import kotlinx.coroutines.*

class HomeViewModel : ViewModel() {
    val viewItems = MutableLiveData<ArrayList<ViewItem>>()
    private var viewItemList: ArrayList<ViewItem> = arrayListOf()

    private var productItems: ArrayList<Product> = arrayListOf()
    private var recommendsItems: MutableList<ArrayList<RecommendProduct>> = mutableListOf()

    private val _dataLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>  // 더 불러오기할 때 Loading 표시를 위한 LiveData
        get() = _dataLoading

    private val _snackBarMessage = MutableLiveData<Event<String>>()
    val snackBarMessage: LiveData<Event<String>>    // 오류 발생 시 SnackBar 메시지 표시를 위한 LiveData
        get() = _snackBarMessage

    private val _frontMessage = MutableLiveData<Event<String>>()
    val frontMessage: LiveData<Event<String>>   // 최초 로드 실패 시 전면 오류 화면에 표시할 메시지를 위한 LiveData
        get() = _frontMessage

    private val _occurLoadError = MutableLiveData<Boolean>()
    val occurLoadError: LiveData<Boolean>   // 전면 오류 화면 Visibility를 위한 LiveData
        get() = _occurLoadError

    private val glowPickAPI = GlowPickAPI.create()
    private var currentPageNumber = 1   // 제품 페이지 번호는 1부터 시작

    init {
        getData()
    }

    /**
     * 최초 데이터(상품 정보, 추천상품 정보) 불러오기
     */
    private fun getData() {
        viewItemList.clear()

        viewModelScope.launch {
            supervisorScope {
                val deferreds = listOf(
                    async { glowPickAPI.getProducts(currentPageNumber) },
                    async { glowPickAPI.getRecommends() }
                )

                try {
                    val result = deferreds.awaitAll()

                    val productResponse = result[0]
                    val recommendResponse = result[1]

                    if (productResponse is ProductResponse && recommendResponse is LinkedTreeMap<*, *>) {
                        productItems = productResponse.products

                        recommendResponse.values.forEach {
                            if (it is ArrayList<*>) {
                                val list = ArrayList<RecommendProduct>()
                                it.forEach { item ->
                                    if (item is RecommendProduct) {
                                        list.add(item)
                                    }
                                }

                                recommendsItems.add(list)
                            }
                        }
                        makeViewItems()
                    }
                } catch (e: Exception) {
                    error(e.message ?: "")
                }
            }
        }
    }

    /**
     * 상품 정보 더 불러오기
     */
    fun moreProductList() {
        if (currentPageNumber == 1)
            return

        _dataLoading.value = true

        viewModelScope.launch {
            supervisorScope {
                val deferred = async { glowPickAPI.getProducts(currentPageNumber) }

                try {
                    val result = deferred.await()
                    _dataLoading.value = false  // 로딩 먼저 닫기

                    productItems = result.products
                    makeViewItems()

                } catch (e: Exception) {
                    _dataLoading.value = false
                    error(e.message ?: "")
                }
            }
        }
    }

    /**
     * 오류 전면 화면(최초 로드 실패시 띄움)의 재시도 버튼 클릭 이벤트를 처리한다
     */
    fun clickRetryButton() {
        _occurLoadError.value = false

        getData()
    }

    /**
     * 상품 정보와 추천 상품 정보를 [viewItemList]로 구성한다
     * 한 페이지마다 20개의 상품 정보 + 1개의 추천 제품 리스트로 구성한다
     */
    private fun makeViewItems() {
        productItems.forEach {
            viewItemList.add(ViewItem(any = it))
        }

        if (currentPageNumber <= recommendsItems.size) {
            val recommends = RecommendsViewItem(currentPageNumber, recommendsItems[currentPageNumber - 1])
            viewItemList.add(ViewItem(any = recommends, viewType = ViewType.RECOMMENDS))
        }

        currentPageNumber++
        viewItems.value = viewItemList
    }

    /**
     * 에러 처리
     * 최초 데이터 불러오기 실패할 경우 전면 오류메시지 화면을 띄우고 그 외에는 스낵바로 오류메시지를 띄운다
     */
    private fun error(errorMessage: String) {
        if (currentPageNumber == 1) { // 초기화 실패한 경우 전면 오류메시지 화면 표시
            _frontMessage.value = Event(errorMessage)
            _occurLoadError.value = true
        } else {    // 그외 스낵바 표시
            _snackBarMessage.value = Event(errorMessage)
        }
    }
}