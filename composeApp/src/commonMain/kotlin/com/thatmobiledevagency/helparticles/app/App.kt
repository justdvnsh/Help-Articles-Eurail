package com.thatmobiledevagency.helparticles.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thatmobiledevagency.helparticles.articles.presentation.articleDetails.ArticleDetailPage
import com.thatmobiledevagency.helparticles.articles.presentation.articleDetails.ArticleDetailViewModel
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.ArticleListPage
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.ArticleListViewModel
import com.thatmobiledevagency.helparticles.core.presentation.AppTheme
import com.thatmobiledevagency.helparticles.settings.presentation.SettingsPage
import com.thatmobiledevagency.helparticles.settings.presentation.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    AppTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.HelpArticlesGraph
        ) {
            navigation<Routes.HelpArticlesGraph>(
                startDestination = Routes.HelpArticlesList
            ) {
                composable<Routes.HelpArticlesList> {
                    ArticleListPage(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.background
                            )
                            .padding(12.dp)
                            .statusBarsPadding(),
                        viewModel = koinViewModel<ArticleListViewModel>(),
                        onArticleClick = { article ->
                            navController.navigate(
                                Routes.HelpArticlesDetail(article.id)
                            )
                        },
                        onSettingsClicked = {
                            navController.navigate(Routes.Settings)
                        }
                    )
                }

                composable<Routes.HelpArticlesDetail> { entry ->
                    val args = entry.toRoute<Routes.HelpArticlesDetail>()
                    val viewModel = koinViewModel<ArticleDetailViewModel>()

                    LaunchedEffect(args.id) {
                        viewModel.loadArticle(args.id)
                    }

                    ArticleDetailPage(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .background(color = MaterialTheme.colorScheme.background)
                            .statusBarsPadding(),
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<Routes.Settings> {
                    SettingsPage(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.background
                            )
                            .padding(12.dp)
                            .statusBarsPadding(),
                        viewModel = koinViewModel<SettingsViewModel>(),
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}