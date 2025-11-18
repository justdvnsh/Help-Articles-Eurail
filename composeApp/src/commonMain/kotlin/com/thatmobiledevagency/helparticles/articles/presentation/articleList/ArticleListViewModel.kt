package com.thatmobiledevagency.helparticles.articles.presentation.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import com.thatmobiledevagency.helparticles.core.domain.onError
import com.thatmobiledevagency.helparticles.core.domain.onSuccess
import com.thatmobiledevagency.helparticles.core.presentation.UiText
import com.thatmobiledevagency.helparticles.core.presentation.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleListViewModel(
    private val konnectivity: Konnectivity,
    private val articleRepository: ArticleRepository
): ViewModel() {

    private var searchJob: Job? = null

    private val _state = MutableStateFlow(ArticleListState())
    val state = _state
        .onStart {
            getArticles()
            observeSearchQuery()
            observeConnectivity()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: ArticleListActions) {
        when(action) {
            is ArticleListActions.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        isLoading = true,
                        searchQuery = action.query,
                        errorMessage = null
                    )
                }
            }
            ArticleListActions.OnRefreshClicked -> {
                _state.update {
                    it.copy(
                        isLoading = true,
                        results = emptyList(),
                        errorMessage = null
                    )
                }
                getArticles()
            }
            else -> {
                // do nothing.
            }
        }
    }

    private fun observeConnectivity() = viewModelScope.launch {
        konnectivity
            .isConnectedState
            .filter { it }
            .collect {
                observeSearchQuery()
            }
    }

    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(5000L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        // could show a cached version or previously searched terms.
                    }

                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = searchArticles(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchArticles(query: String) = viewModelScope.launch {
        _state.update {
            it.copy(isLoading = true)
        }

        articleRepository
            .searchArticles(query)
            .onSuccess { results ->
                _state.update {
                    it.copy(
                        results = results,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText()
                    )
                }
            }
    }

    private fun getArticles() = viewModelScope.launch {
        _state.update {
            it.copy(isLoading = true)
        }

        articleRepository
            .getArticles()
            .onSuccess { results ->
                _state.update {
                    it.copy(
                        results = results,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage = error.toUiText()
                    )
                }
            }
    }
}