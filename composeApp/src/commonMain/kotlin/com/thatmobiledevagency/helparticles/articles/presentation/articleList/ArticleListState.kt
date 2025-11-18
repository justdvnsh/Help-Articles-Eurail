package com.thatmobiledevagency.helparticles.articles.presentation.articleList

import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.core.presentation.UiText

data class ArticleListState(
    val searchQuery: String = "",
    val searchResults: List<Article> = emptyList(),
    val results: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)