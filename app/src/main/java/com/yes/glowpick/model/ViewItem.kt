package com.yes.glowpick.model


data class ViewItem(
    val viewType: ViewType = ViewType.PRODUCT,
    val any: Any? = null
)

enum class ViewType{
    PRODUCT,
    RECOMMENDS,
    LOADING
}


