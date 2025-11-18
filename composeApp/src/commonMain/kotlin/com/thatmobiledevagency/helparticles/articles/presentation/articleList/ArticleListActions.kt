package com.thatmobiledevagency.helparticles.articles.presentation.articleList

import com.thatmobiledevagency.helparticles.articles.domain.Article

sealed interface ArticleListActions {
    data class OnSearchQueryChange(val query: String): ArticleListActions
    data class OnArticleClicked(val article: Article): ArticleListActions
    data object OnRefreshClicked: ArticleListActions
}