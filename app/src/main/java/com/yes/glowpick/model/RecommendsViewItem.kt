package com.yes.glowpick.model

import com.yes.glowpick.model.recommend.RecommendProduct

/**
 * 추천 제품 리스트를 위한 View Data Class
 */
data class RecommendsViewItem(
    val index: Int,
    val items:ArrayList<RecommendProduct>,
)
