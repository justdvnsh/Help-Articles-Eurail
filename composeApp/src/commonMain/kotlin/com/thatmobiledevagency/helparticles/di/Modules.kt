package com.thatmobiledevagency.helparticles.di

import com.plusmobileapps.konnectivity.Konnectivity
import com.thatmobiledevagency.cache.CacheDataSource
import com.thatmobiledevagency.cache.CacheStore
import com.thatmobiledevagency.cache.HelpArticleCache
import com.thatmobiledevagency.helparticles.articles.data.network.RemoteArticleDataSource
import com.thatmobiledevagency.helparticles.articles.data.network.RemoteArticleDataSourceImpl
import com.thatmobiledevagency.helparticles.articles.data.repository.ArticleRepositoryImpl
import com.thatmobiledevagency.helparticles.articles.domain.ArticleRepository
import com.thatmobiledevagency.helparticles.articles.presentation.articleDetails.ArticleDetailViewModel
import com.thatmobiledevagency.helparticles.articles.presentation.articleList.ArticleListViewModel
import com.thatmobiledevagency.helparticles.settings.presentation.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

expect val platformModule: Module

@OptIn(ExperimentalTime::class)
val sharedModules = module{

    singleOf(::RemoteArticleDataSourceImpl).bind<RemoteArticleDataSource>()
    singleOf(::CacheStore).bind<HelpArticleCache>()
    single {
        CacheDataSource(
            store = get(),
            nowMsProvider = { Clock.System.now().toEpochMilliseconds() }
        )
    }
    singleOf(::ArticleRepositoryImpl).bind<ArticleRepository>()

    single { Konnectivity() }

    viewModelOf(::ArticleListViewModel)
    viewModelOf(::ArticleDetailViewModel)
    viewModelOf(::SettingsViewModel)
}