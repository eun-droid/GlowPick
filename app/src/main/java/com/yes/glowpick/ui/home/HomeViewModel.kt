package com.yes.glowpick.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yes.glowpick.GlowPickAPI
import com.yes.glowpick.model.RecommendsViewItem
import com.yes.glowpick.model.ViewItem
import com.yes.glowpick.model.ViewType
import com.yes.glowpick.model.product.Product
import com.yes.glowpick.model.product.ProductResponse
import com.yes.glowpick.model.recommend.RecommendProduct
import com.yes.glowpick.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    val viewItems = MutableLiveData<ArrayList<ViewItem>>()
    private var viewItemList: ArrayList<ViewItem> = arrayListOf()

    private var productItems: ArrayList<Product> = arrayListOf()
    private lateinit var recommendsItems: MutableList<ArrayList<RecommendProduct>>

    private val _dataLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _dataLoading

    private val _snackBarMessage = MutableLiveData<Event<String>>()
    val snackBarMessage: LiveData<Event<String>>
        get() = _snackBarMessage

    private val _frontMessage = MutableLiveData<Event<String>>()
    val frontMessage: LiveData<Event<String>>
        get() = _frontMessage

    private val _occurLoadError = MutableLiveData<Boolean>()
    val occurLoadError: LiveData<Boolean>
        get() = _occurLoadError

    private val glowPickAPI = GlowPickAPI.create()
    private var currentPageNumber = 1   // 제품 페이지 번호는 1부터 시작

    private var checkListForResponse = MutableList(RequireDataType.values().size){ DataReceiveStatus.WAITING }
    private var alreadyErrorOccur: Boolean = false

    /* An enum class that specifies all the data types needed */
    enum class RequireDataType {
        PRODUCT,
        RECOMMEND
    }

    enum class DataReceiveStatus {
        WAITING,
        SUCCESS,
        FAIL
    }

    init {
        getData()
    }

    private fun getData() {
        alreadyErrorOccur = false
        checkListForResponse.fill(DataReceiveStatus.WAITING)

        if (!::recommendsItems.isInitialized) {
            getRecommendList()
        } else {
            checkListForResponse[RequireDataType.RECOMMEND.ordinal] = DataReceiveStatus.SUCCESS
        }

        getProductList()
    }

    private fun getRecommendList() {
        glowPickAPI.getRecommends().enqueue(object: Callback<Map<String, ArrayList<RecommendProduct>>> {
            override fun onResponse(
                call: Call<Map<String, ArrayList<RecommendProduct>>>,
                response: Response<Map<String, ArrayList<RecommendProduct>>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        recommendsItems = it.values.toMutableList()

                        submitResponse(RequireDataType.RECOMMEND, DataReceiveStatus.SUCCESS)
                    }
                } else {
                    error(response.message() ?: "")
                    submitResponse(RequireDataType.RECOMMEND, DataReceiveStatus.FAIL)
                }
            }

            override fun onFailure(call: Call<Map<String, ArrayList<RecommendProduct>>>, t: Throwable) {
                error(t.message ?: "")
                submitResponse(RequireDataType.RECOMMEND, DataReceiveStatus.FAIL)
            }
        })
    }

    private fun getProductList() {
        glowPickAPI.getProducts(currentPageNumber).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {

                if (response.isSuccessful) {
                    response.body()?.products?.let {
                        productItems = it
                        submitResponse(RequireDataType.PRODUCT, DataReceiveStatus.SUCCESS)
                    }
                } else {
                    error(response.message() ?: "")
                    submitResponse(RequireDataType.PRODUCT, DataReceiveStatus.FAIL)
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                error(t.message ?: "")
                submitResponse(RequireDataType.PRODUCT, DataReceiveStatus.FAIL)
            }
        })
    }

    fun moreProductList() {
        _dataLoading.value = true
        getData()
    }

    fun clickRetryButton() {
        _occurLoadError.value = false
        getData()
    }

    private fun submitResponse(requireDataType: RequireDataType, dataReceiveStatus: DataReceiveStatus) {
        checkListForResponse[requireDataType.ordinal] = dataReceiveStatus

        try {
            if (checkListForResponse.all {
                    it != DataReceiveStatus.WAITING }) {
                _dataLoading.value = false  // 모든 데이터 응답 완료

                if (checkListForResponse.all {
                        it == DataReceiveStatus.SUCCESS }) {

                    makeViewItems()
                    viewItems.value = viewItemList

                } else {
                    throw Exception("일부 데이터를 가져오는데 실패했습니다.")
                }

                currentPageNumber++
            }
        } catch (e: Exception) {
            error(e.message ?: "")
        }
    }

    private fun makeViewItems() {
        productItems.forEach {
            viewItemList.add(ViewItem(any = it))
        }

        if (currentPageNumber <= recommendsItems.size) {
            val recommends = RecommendsViewItem(currentPageNumber, recommendsItems[currentPageNumber - 1])
            viewItemList.add(ViewItem(any = recommends, viewType = ViewType.RECOMMENDS))
        }
    }

    private fun error(errorMessage: String) {
        if (alreadyErrorOccur)
            return

        if (currentPageNumber == 1) { // 초기화 실패한 경우 전면 오류메시지 화면 표시
            _frontMessage.value = Event(errorMessage)
            _occurLoadError.value = true
        } else {    // 그외 스낵바 표시
            _snackBarMessage.value = Event(errorMessage)
        }

        alreadyErrorOccur = true
    }
}