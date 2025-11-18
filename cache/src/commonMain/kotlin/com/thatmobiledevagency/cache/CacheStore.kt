package com.thatmobiledevagency.cache

class CacheStore: HelpArticleCache {
    private val map = mutableMapOf<String, String?>()

    override suspend fun get(key: String): String? = map[key]

    override suspend fun put(key: String, json: String) {
        map[key] = json
    }

    override suspend fun remove(key: String) {
        map.remove(key)
    }

    override suspend fun clear() {
        map.clear()
    }

}