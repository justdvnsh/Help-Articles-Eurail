package com.thatmobiledevagency.helparticles.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thatmobiledevagency.cache.CacheDataSource
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RefreshArticlesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val articleRepository: ArticleRepository by inject()
    private val cacheDataSource: CacheDataSource by inject()

    override suspend fun doWork(): Result {
        return try {
            if (cacheDataSource.isListFresh()) {
                Result.success()
            } else {
                val result = articleRepository.getArticles()
                when (result) {
                    is com.thatmobiledevagency.helparticles.core.domain.Result.Success -> {
                        Result.success()
                    }
                    is com.thatmobiledevagency.helparticles.core.domain.Result.Error -> {
                        Result.retry()
                    }
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}