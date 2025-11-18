package com.thatmobiledevagency.cache

import com.thatmobiledevagency.cache.models.CacheWrapper
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheDataSourceTest {

    private val SAMPLE_JSON = """[{"id":"a1","title":"First"}]"""

    @Test
    fun cachedEntry_isFresh_beforeTtl() = runTest {
        val baseNow = 1_700_000_000_000L
        val store = CacheStore()

        val cache = CacheDataSource(store = store, nowMsProvider = { baseNow })
        cache.putListJson(SAMPLE_JSON)

        assertTrue(cache.hasListCache(), "Cache should exist after putListJson")

        val oneHourMs = 60L * 60L * 1000L
        assertTrue(cache.isListFresh(oneHourMs), "Entry should be fresh when now == cachedAt")
    }

    @Test
    fun cachedEntry_isStale_afterTtl() = runTest {
        val baseNow = 1_700_000_000_000L
        val staleNow = baseNow + (10 * 60 * 60 * 1000L) // 10 hours later

        val store = CacheStore()

        val writer = CacheDataSource(store = store, nowMsProvider = { baseNow })
        writer.putListJson(SAMPLE_JSON)

        val reader = CacheDataSource(store = store, nowMsProvider = { staleNow })

        val sixHoursMs = 6L * 60L * 60L * 1000L
        assertFalse(reader.isListFresh(sixHoursMs), "Cache should be stale after 10 hours")
    }

    @Test
    fun cachedEntry_isFresh_withinTtl() = runTest {
        val baseNow = 2_000_000_000_000L
        val laterNow = baseNow + (1 * 60 * 60 * 1000L) // 1 hour later

        val store = CacheStore()

        val writer = CacheDataSource(store = store, nowMsProvider = { baseNow })
        writer.putListJson(SAMPLE_JSON)

        val reader = CacheDataSource(store = store, nowMsProvider = { laterNow })

        val sixHoursMs = 6L * 60L * 60L * 1000L
        assertTrue(reader.isListFresh(sixHoursMs), "Cache should be fresh within 1 hour")
    }

    @Test
    fun isListFresh_false_when_no_cache_exists() = runTest {
        val store = CacheStore()
        val cache = CacheDataSource(store = store)

        assertFalse(cache.isListFresh(), "No cache should return false for freshness")
    }
}
