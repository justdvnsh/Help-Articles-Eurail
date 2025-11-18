package com.thatmobiledevagency.helparticles.articles.data.models

import com.thatmobiledevagency.helparticles.articles.domain.Article
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailResponse(
    val article: Article
)