package com.thatmobiledevagency.helparticles.articles.domain

import com.thatmobiledevagency.helparticles.core.domain.DataError
import com.thatmobiledevagency.helparticles.core.domain.Result

interface ArticleRepository {
    suspend fun getArticles(): Result<List<Article>, DataError.RemoteErrors>
    suspend fun getArticleDetails(id: String): Result<Article?, DataError.RemoteErrors>

    // search
    suspend fun searchArticles(query: String): Result<List<Article>, DataError.RemoteErrors>
}