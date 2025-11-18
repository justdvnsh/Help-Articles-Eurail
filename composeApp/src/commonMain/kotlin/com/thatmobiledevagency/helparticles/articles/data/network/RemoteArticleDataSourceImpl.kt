package com.thatmobiledevagency.helparticles.articles.data.network

import com.thatmobiledevagency.helparticles.articles.data.models.ArticleDetailResponse
import com.thatmobiledevagency.helparticles.articles.data.models.ArticlesResponse
import com.thatmobiledevagency.helparticles.articles.domain.Article
import com.thatmobiledevagency.helparticles.core.data.safeCall
import com.thatmobiledevagency.helparticles.core.domain.DataError
import com.thatmobiledevagency.helparticles.core.domain.Result
import com.thatmobiledevagency.helparticles.core.domain.map
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class RemoteArticleDataSourceImpl: RemoteArticleDataSource {
    @OptIn(InternalAPI::class)
    override suspend fun getHelpArticles(): Result<List<Article>, DataError.RemoteErrors> {
        return safeCall<ArticlesResponse> {
            // Simulate network delay
            delay(Random.nextLong(300, 1500))
            when (Random.nextFloat()) {
                in 0f..0.70f -> {
                    // 70% success
                    createMockResponse(
                        status = HttpStatusCode.OK,
                        body = Json.encodeToString(
                            ArticlesResponse.serializer(),
                            ArticlesResponse(articles = mockArticles)
                        )
                    )

                }
                in 0.70f..0.85f -> {
                    // 15% server error
                    createMockResponse(status = HttpStatusCode.InternalServerError, body = null)
                }
                in 0.85f..0.95f -> {
                    // 10% timeout
                    delay(6000)
                    throw SocketTimeoutException("Request timeout")
                }
                else -> {
                    // 5% no internet
                    throw UnresolvedAddressException()
                }
            }
        }.map { it.articles }
    }

    @OptIn(InternalAPI::class)
    override suspend fun getArticleDetail(id: String): Result<Article, DataError.RemoteErrors> {
        return safeCall<ArticleDetailResponse> {
            delay(Random.nextLong(300, 1000))

            val article = mockArticles.find { it.id == id }

            when (Random.nextFloat()) {
                in 0f..0.70f -> {
                    if (article != null) {
                        createMockResponse(
                            status = HttpStatusCode.OK,
                            body = Json.encodeToString(
                                ArticleDetailResponse.serializer(),
                                ArticleDetailResponse(article = article)
                            )
                        )
                    } else {
                        createMockResponse(status = HttpStatusCode.NotFound, body = null)
                    }
                }
                in 0.70f..0.85f -> {
                    createMockResponse(status = HttpStatusCode.InternalServerError, body = null)
                }
                in 0.85f..0.95f -> {
                    delay(6000)
                    throw SocketTimeoutException("Request timeout")
                }
                else -> {
                    throw UnresolvedAddressException()
                }
            }
        }.map { it.article }
    }

    @OptIn(InternalAPI::class)
    override suspend fun searchArticles(query: String): Result<List<Article>, DataError.RemoteErrors> {
        return safeCall<ArticlesResponse> {
            delay(Random.nextLong(300, 1000))

            val articles = mockArticles.filter {
                it.title.contains(query) || it.summary.contains(query)
            }

            when (Random.nextFloat()) {
                in 0f..0.70f -> {
                    createMockResponse(
                        status = HttpStatusCode.OK,
                        body = Json.encodeToString(
                            ArticlesResponse.serializer(),
                            ArticlesResponse(articles = articles)
                        )
                    )
                }
                in 0.70f..0.85f -> {
                    createMockResponse(status = HttpStatusCode.InternalServerError, body = null)
                }
                in 0.85f..0.95f -> {
                    delay(6000)
                    throw SocketTimeoutException("Request timeout")
                }
                else -> {
                    throw UnresolvedAddressException()
                }
            }
        }.map { it.articles }
    }

    @InternalAPI
    private fun createMockResponse(
        status: HttpStatusCode,
        body: String?
    ) : HttpResponse {

        return object: HttpResponse() {
            override val status: HttpStatusCode = status

            override val coroutineContext: CoroutineContext = CoroutineScope(Dispatchers.Default).coroutineContext

            override val call: HttpClientCall = HttpClientCall(HttpClient())

            override val headers: Headers = headersOf("Content-Type", "application/json")

            override val requestTime: GMTDate = GMTDate()

            override val responseTime: GMTDate = GMTDate()

            override val version: HttpProtocolVersion = HttpProtocolVersion.HTTP_1_1

            override val content: ByteReadChannel = ByteReadChannel(body ?: "")
        }
    }

}