package com.thatmobiledevagency.helparticles.articles.presentation.articleList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.composables.ArticleList
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.composables.SearchBar
import helparticles.composeapp.generated.resources.Res
import helparticles.composeapp.generated.resources.app_title
import helparticles.composeapp.generated.resources.no_articles_found
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArticleListPage(
    modifier: Modifier = Modifier,
    viewModel: ArticleListViewModel = koinViewModel(),
    onArticleClick: (Article) -> Unit,
    onSettingsClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ArticleListContent(
        modifier,
        state,
        onAction = { actions ->
            when (actions) {
                is ArticleListActions.OnArticleClicked ->
                    onArticleClick(actions.article)
                ArticleListActions.OnRefreshClicked ->
                    viewModel.onAction(ArticleListActions.OnRefreshClicked)
                is ArticleListActions.OnSearchQueryChange ->
                    viewModel.onAction(ArticleListActions.OnSearchQueryChange(actions.query))
            }
        },
        onSettingsClicked = onSettingsClicked
    )
}

@Composable
private fun ArticleListContent(
    modifier: Modifier = Modifier,
    state: ArticleListState,
    onAction: (ArticleListActions) -> Unit,
    onSettingsClicked: () -> Unit
) {
    val kbController = LocalSoftwareKeyboardController.current

    val articleResultListState = rememberLazyListState()

    LaunchedEffect(state.results) {
        articleResultListState.animateScrollToItem(0)
    }

    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        onSettingsClicked()
                    },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings"
                    )
                }
                IconButton(
                    onClick = {
                        onAction(ArticleListActions.OnRefreshClicked)
                    },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        }
        SearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = { query ->
                onAction(ArticleListActions.OnSearchQueryChange(query))
            },
            onImeSearch = {
                kbController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (state.errorMessage != null){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage.asString(),
                    modifier = Modifier.testTag("error_message")
                )
            }
        } else if (state.results.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.no_articles_found)
                )
            }
        } else {
            ArticleList(
                articles = state.results,
                onArticleClick = { article ->
                    onAction(ArticleListActions.OnArticleClicked(article))
                },
                modifier = Modifier.fillMaxSize(),
                scrollState = articleResultListState
            )
        }
    }

}

@Preview
@Composable
fun ArticleListPagePreview() {
    ArticleListPage(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        viewModel = koinViewModel<ArticleListViewModel>(),
        onArticleClick = {},
        onSettingsClicked = {}
    )
}