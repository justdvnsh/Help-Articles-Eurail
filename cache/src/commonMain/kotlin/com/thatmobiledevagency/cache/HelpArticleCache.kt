package com.thatmobiledevagency.cache

interface HelpArticleCache {
    suspend fun get(key: String): String?
    suspend fun put(key: String, jsonValue: String)
    suspend fun remove(key: String)
    suspend fun clear()
}