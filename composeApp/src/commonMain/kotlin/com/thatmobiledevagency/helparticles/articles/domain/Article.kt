package com.thatmobiledevagency.helparticles.articles.domain

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val contentHtml: String,
    val updatedAt: Long
)
