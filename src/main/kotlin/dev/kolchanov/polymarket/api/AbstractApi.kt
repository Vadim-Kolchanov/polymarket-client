package dev.kolchanov.polymarket.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.kolchanov.polymarket.exception.ApiException
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Abstract base class for API clients with HTTP request handling.
 *
 * Provides common functionality for making HTTP requests using OkHttp,
 * JSON serialization/deserialization with Jackson, and coroutine-based async execution.
 *
 * @property baseUrl Base URL for all API requests.
 * @property interceptors List of OkHttp interceptors to apply to requests.
 * @property client Configured OkHttp client instance.
 * @property objectMapper Jackson ObjectMapper for JSON processing.
 * @property coroutineScope Coroutine scope for async operations.
 */
abstract class AbstractApi(
    val baseUrl: String,
    val interceptors: List<Interceptor> = emptyList(),
    val client: OkHttpClient = OkHttpClient.Builder()
        .apply { interceptors.forEach(::addInterceptor) }
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.MILLISECONDS)
        .build(),
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule()),
    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {

    companion object {
        /** Empty path constant for requests to base URL. */
        const val EMPTY_PATH = ""
        /** JSON media type for Content-Type header. */
        val JSON: MediaType = "application/json".toMediaType()

        private val OVERLOAD_HEADERS_BY_NAME: Map<String, String> = mapOf(
            HttpHeaderNames.USER_AGENT to "@polymarket/clob-client",
            HttpHeaderNames.ACCEPT to "*/*",
            HttpHeaderNames.CONNECTION to "keep-alive",
            HttpHeaderNames.CONTENT_TYPE to JSON.toString(),
        ).mapKeys { it.key.toString() }
    }

    /** Default request builder with base URL and standard headers. */
    val defaultRequest: Request = Request.Builder()
        .url(baseUrl)
        .apply { OVERLOAD_HEADERS_BY_NAME.forEach { addHeader(it.key, it.value) } }
        .build()

    /**
     * Performs an asynchronous HTTP POST request.
     *
     * @param T Expected response type.
     * @param path URL path to append to base URL.
     * @param body Request body object (will be serialized to JSON).
     * @return [Deferred] with deserialized response.
     * @throws ApiException if the request fails.
     */
    inline fun <reified T> post(path: String, body: Any?): Deferred<T> = coroutineScope.async {
        defaultRequest.newBuilder()
            .url("$baseUrl$path")
            .post(body.toRequestBody())
            .build()
            .let(::executeRequest)
    }

    /**
     * Performs an asynchronous HTTP GET request.
     *
     * @param T Expected response type.
     * @param path URL path to append to base URL.
     * @return [Deferred] with deserialized response.
     * @throws ApiException if the request fails.
     */
    inline fun <reified T> get(path: String): Deferred<T> = coroutineScope.async {
        defaultRequest.newBuilder()
            .url("$baseUrl$path")
            .get()
            .build()
            .let(::executeRequest)
    }

    /**
     * Performs an asynchronous HTTP DELETE request.
     *
     * @param T Expected response type.
     * @param path URL path to append to base URL.
     * @param body Request body object (will be serialized to JSON).
     * @return [Deferred] with deserialized response.
     * @throws ApiException if the request fails.
     */
    inline fun <reified T> delete(path: String, body: Any?): Deferred<T> = coroutineScope.async {
        defaultRequest.newBuilder()
            .url("$baseUrl$path")
            .delete(body.toRequestBody())
            .build()
            .let(::executeRequest)
    }

    /**
     * Executes an HTTP request and deserializes the response.
     *
     * @param T Expected response type.
     * @param request The HTTP request to execute.
     * @return Deserialized response object.
     * @throws ApiException if the response status is not successful.
     */
    inline fun <reified T> executeRequest(request: Request): T {
        client.newCall(request).execute().use { response ->
            val body = response.body.string()
            if (response.isSuccessful.not()) {
                throw ApiException(
                    "Request failed: ${response.code} - $body",
                    status = HttpResponseStatus.valueOf(response.code),
                )
            }
            return objectMapper.readValue(body)
        }
    }

    /**
     * Converts any object to a JSON [RequestBody].
     *
     * @return JSON request body, or [RequestBody.EMPTY] if null.
     */
    fun Any?.toRequestBody(): RequestBody =
        this?.let(objectMapper::writeValueAsString)?.toRequestBody(JSON)
            ?: RequestBody.EMPTY
}
