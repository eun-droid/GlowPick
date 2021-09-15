package com.yes.glowpick.model

import com.yes.glowpick.model.recommend.RecommendProduct

data class RecommendsViewItem(
    val index: Int,
    val items:ArrayList<RecommendProduct>,
)
