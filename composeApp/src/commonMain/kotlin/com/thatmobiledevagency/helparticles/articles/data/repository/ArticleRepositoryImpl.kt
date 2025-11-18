package com.thatmobiledevagency.helparticles.articles.data.repository

import com.thatmobiledevagency.cache.CacheDataSource
import com.thatmobiledevagency.helparticles.articles.data.models.ArticlesResponse
import com.thatmobiledevagency.helparticles.articles.data.network.RemoteArticleDataSource
import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import com.thatmobiledevagency.helparticles.core.domain.DataError
import com.thatmobiledevagency.helparticles.core.domain.Result
import com.thatmobiledevagency.helparticles.core.domain.SettingsConstants
import kotlinx.serialization.json.Json

class ArticleRepositoryImpl(
    private val remoteArticleDataSource: RemoteArticleDataSource,
    private val helpArticleCache: CacheDataSource
) : ArticleRepository {

    private val json = Json {ignoreUnknownKeys = true}

    override suspend fun getArticles(): Result<List<Article>, DataError.RemoteErrors> {
        if (helpArticleCache.isListFresh() && !SettingsConstants.cacheDisabled) {
            val cachedJson = helpArticleCache.getListJson()
            if (cachedJson != null) {
                return try {
                    val articlesResponse = json.decodeFromString(ArticlesResponse.serializer(), cachedJson)
                    println("Fetching article list from cache")
                    Result.Success(articlesResponse.articles)
                } catch (e: Exception) {
                    helpArticleCache.clear()
                    fetchAndCacheArticles()
                }
            }
        }

        val remoteResult = fetchAndCacheArticles()

        // If remote fetch failed (e.g., no internet) but we have cache (even stale), return it as fallback
        if (remoteResult is Result.Error && !SettingsConstants.cacheDisabled) {
            val cachedJson = helpArticleCache.getListJson()
            if (cachedJson != null) {
                return try {
                    val articlesResponse = json.decodeFromString(ArticlesResponse.serializer(), cachedJson)
                    println("Remote fetch failed, returning stale cache as fallback")
                    Result.Success(articlesResponse.articles)
                } catch (e: Exception) {
                    remoteResult
                }
            }
        }

        return remoteResult
    }

    private suspend fun fetchAndCacheArticles(): Result<List<Article>, DataError.RemoteErrors> {
        val result = remoteArticleDataSource.getHelpArticles()

        if (result is Result.Success) {
            try {
                val articlesResponse = ArticlesResponse(articles = result.data)
                val jsonString = json.encodeToString(ArticlesResponse.serializer(), articlesResponse)
                helpArticleCache.putListJson(jsonString)
            } catch (e: Exception) {
                // do nothing.
            }
        }

        println("Fetching article list from remote")
        return result
    }

    override suspend fun getArticleDetails(id: String): Result<Article?, DataError.RemoteErrors> {
        return remoteArticleDataSource.getArticleDetail(id)
    }

    override suspend fun searchArticles(query: String): Result<List<Article>, DataError.RemoteErrors> {
        return remoteArticleDataSource.searchArticles(query)
    }

}