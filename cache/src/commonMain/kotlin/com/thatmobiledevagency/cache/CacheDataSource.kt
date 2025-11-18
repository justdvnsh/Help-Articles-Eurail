package com.thatmobiledevagency.cache

import com.thatmobiledevagency.cache.models.CacheWrapper
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CacheDataSource @OptIn(ExperimentalTime::class) constructor(
    private val store: HelpArticleCache,
    private val nowMsProvider: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    private val mutex = Mutex()
    val json = Json {ignoreUnknownKeys = true}


    companion object Companion {
        private const val KEY_LIST = "help_articles_cache_list"
        private const val KEY_DETAIL = "help_articles_cache_detail"

        const val DEFAULT_LIST_TTL_MS: Long = 6 * 60 * 60 * 1000L
        const val DEFAULT_DETAIL_TTL_MS: Long = 24 * 60 * 60 * 1000L
    }

    private suspend fun getWrapped(key: String): CacheWrapper? {
        val raw = store.get(key) ?: return null
        return try {
            json.decodeFromString(CacheWrapper.serializer(), raw)
        } catch (t: Throwable) {
            // corrupt -> remove and return null
            store.remove(key)
            null
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun putWrapped(key: String, jsonStr: String) {
        val wrapper = CacheWrapper(json = jsonStr, cachedAt = nowMsProvider())
        store.put(key, json.encodeToString(CacheWrapper.serializer(), wrapper))
    }

    suspend fun clear() {
        mutex.withLock {
            store.clear()
        }
    }

    suspend fun getListJson(): String? = mutex.withLock { getWrapped(KEY_LIST)?.json }

    suspend fun putListJson(listJson: String) = mutex.withLock { putWrapped(KEY_LIST, listJson) }

    suspend fun getListCachedAt(): Long? = mutex.withLock { getWrapped(KEY_LIST)?.cachedAt }

    suspend fun hasListCache(): Boolean {
        return getListJson() != null
    }

    @OptIn(ExperimentalTime::class)
    suspend fun isListFresh(ttlMs: Long = DEFAULT_LIST_TTL_MS): Boolean {
        val cachedAt = getListCachedAt() ?: return false
        return (nowMsProvider() - cachedAt) <= ttlMs
    }
}