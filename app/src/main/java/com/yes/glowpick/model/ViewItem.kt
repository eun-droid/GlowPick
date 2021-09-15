package com.yes.glowpick.model

/**
 * [ViewType]에 포함된 아이템을 담기 위한 Data Class
 */
data class ViewItem(
    val viewType: ViewType = ViewType.PRODUCT,
    val any: Any? = null
)

enum class ViewType{
    PRODUCT,
    RECOMMENDS,
    LOADING
}


