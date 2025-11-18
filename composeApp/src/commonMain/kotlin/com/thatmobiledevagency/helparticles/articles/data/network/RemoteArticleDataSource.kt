package com.thatmobiledevagency.helparticles.articles.data.network

import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.core.domain.DataError
import com.thatmobiledevagency.helparticles.core.domain.Result

interface RemoteArticleDataSource {
    suspend fun getHelpArticles(): Result<List<Article>, DataError.RemoteErrors>
    suspend fun getArticleDetail(id: String): Result<Article, DataError.RemoteErrors>

    suspend fun searchArticles(query: String): Result<List<Article>, DataError.RemoteErrors>
}