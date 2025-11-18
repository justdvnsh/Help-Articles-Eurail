package com.thatmobiledevagency.helparticles.articles.presentation.articleDetails

import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.core.presentation.UiText

data class ArticleDetailState(
    val isLoading: Boolean = false,
    val article: Article? = null,
    val errorMessage: UiText? = null
)