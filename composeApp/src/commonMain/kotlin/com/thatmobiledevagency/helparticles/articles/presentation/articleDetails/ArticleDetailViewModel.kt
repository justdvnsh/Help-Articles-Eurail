package com.thatmobiledevagency.helparticles.articles.presentation.articleDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import com.thatmobiledevagency.helparticles.core.domain.onError
import com.thatmobiledevagency.helparticles.core.domain.onSuccess
import com.thatmobiledevagency.helparticles.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleDetailViewModel(
    private val articleRepository: ArticleRepository
): ViewModel() {

    private val _state = MutableStateFlow(ArticleDetailState())
    val state = _state.asStateFlow()

    fun loadArticle(
        id: String
    ) = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)

        articleRepository
            .getArticleDetails(id)
            .onSuccess { result ->
                _state.update {
                    it.copy(
                        article = result,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        article = null,
                        isLoading = false,
                        errorMessage = error.toUiText()
                    )
                }
            }
    }
}