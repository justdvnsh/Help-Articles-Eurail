package com.thatmobiledevagency.cache.models

import kotlinx.serialization.Serializable

@Serializable
data class CacheWrapper(val json: String, val cachedAt: Long)