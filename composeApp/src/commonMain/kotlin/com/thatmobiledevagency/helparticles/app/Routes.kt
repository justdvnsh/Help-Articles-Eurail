package com.thatmobiledevagency.helparticles.app

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object HelpArticlesGraph: Routes

    @Serializable
    data object HelpArticlesList: Routes

    @Serializable
    data class HelpArticlesDetail(val id: String): Routes

    @Serializable
    data object Settings: Routes
}