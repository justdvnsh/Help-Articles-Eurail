package com.thatmobiledevagency.helparticles

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.plusmobileapps.konnectivity.Konnectivity
import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.ArticleListPage
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.ArticleListViewModel
import com.thatmobiledevagency.helparticles.core.domain.DataError
import com.thatmobiledevagency.helparticles.core.presentation.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.thatmobiledevagency.helparticles.core.domain.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle

class ArticleListPageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun errorState_showsErrorAndRefreshButtonTriggersRetry() = runTest {
        val mockRepository = mock<ArticleRepository>()
        val mockKonnectivity = mock<Konnectivity>()

        val connectivityFlow = MutableStateFlow(true)
        whenever(mockKonnectivity.isConnectedState).thenReturn(connectivityFlow)

        // First call returns error, second call returns success
        var callCount = 0
        whenever(mockRepository.getArticles()).thenAnswer {
            callCount++
            if (callCount == 1) {
                Result.Error(DataError.RemoteErrors.NO_INTERNET)
            } else {
                Result.Success(createMockArticles())
            }
        }

        val viewModel = ArticleListViewModel(mockKonnectivity, mockRepository)

        composeTestRule.setContent {
            AppTheme {
                ArticleListPage(
                    viewModel = viewModel,
                    onArticleClick = {},
                    onSettingsClicked = {}
                )
            }
        }

        advanceUntilIdle()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(
            timeoutMillis = 5000
        ) {
            try {
                composeTestRule.onNodeWithText("internet connection", substring = true, ignoreCase = true)
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText("internet connection", substring = true, ignoreCase = true)
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Refresh")
            .assertIsDisplayed()
            .performClick()

        advanceUntilIdle()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(
            timeoutMillis = 5000
        ) {
            try {
                composeTestRule.onNodeWithText("Test Article", substring = true)
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText("Test Article", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun errorState_errorMessageIsVisible() = runTest {
        val mockRepository = mock<ArticleRepository>()
        val mockKonnectivity = mock<Konnectivity>()

        val connectivityFlow = MutableStateFlow(true)
        whenever(mockKonnectivity.isConnectedState).thenReturn(connectivityFlow)

        whenever(mockRepository.getArticles())
            .thenReturn(Result.Error(DataError.RemoteErrors.REQUEST_TIMEOUT))

        val viewModel = ArticleListViewModel(mockKonnectivity, mockRepository)

        composeTestRule.setContent {
            AppTheme {
                ArticleListPage(
                    viewModel = viewModel,
                    onArticleClick = {},
                    onSettingsClicked = {}
                )
            }
        }

        composeTestRule.waitUntil(
            timeoutMillis = 5000,
            condition = {
                try {
                    composeTestRule
                        .onNodeWithTag("error_message")
                        .assertExists()
                        .assertTextContains("timed out", substring = true, ignoreCase = true)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        )

        composeTestRule.onNodeWithText("timed out", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    private fun createMockArticles(): List<Article> {
        return listOf(
            Article(
                id = "1",
                title = "Test Article",
                summary = "This is a test article summary",
                contentHtml = "<p>Test content</p>",
                updatedAt = System.currentTimeMillis()
            ),
            Article(
                id = "2",
                title = "Another Article",
                summary = "Another test summary",
                contentHtml = "<p>More test content</p>",
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}