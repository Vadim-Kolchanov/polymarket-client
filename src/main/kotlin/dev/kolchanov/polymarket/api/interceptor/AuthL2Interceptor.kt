package dev.kolchanov.polymarket.api.interceptor

import dev.kolchanov.polymarket.dto.api.response.PolymarketCredentials
import dev.kolchanov.polymarket.enums.header.PolymarketL2Header
import dev.kolchanov.polymarket.utils.HmacUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor for L2 (API key-based) authentication with Polymarket API.
 *
 * Adds required authentication headers to each request:
 * - [PolymarketL2Header.POLY_ADDRESS] - Wallet address
 * - [PolymarketL2Header.POLY_SIGNATURE] - HMAC signature of the request
 * - [PolymarketL2Header.POLY_TIMESTAMP] - Current Unix timestamp
 * - [PolymarketL2Header.POLY_API_KEY] - API key
 * - [PolymarketL2Header.POLY_PASSPHRASE] - API passphrase
 *
 * @property address Polygon wallet address.
 * @property credentials API credentials containing apiKey, secret, and passphrase.
 *
 * @see HmacUtils.getHmacPolymarketSignature
 * @see PolymarketCredentials
 */
class AuthL2Interceptor(
    private val address: String,
    private val credentials: PolymarketCredentials,
) : Interceptor {

    init {
        require(address.isNotBlank()) { "Address must not be blank" }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val timestamp = System.currentTimeMillis() / 1000
        val valueByHeader = mapOf(
            PolymarketL2Header.POLY_ADDRESS to address,
            PolymarketL2Header.POLY_SIGNATURE to HmacUtils.getHmacPolymarketSignature(
                secret = credentials.secret,
                timestamp = timestamp,
                method = request.method,
                requestPath = request.url.pathSegments.joinToString("/", prefix = "/"),
                body = request.body?.run {
                    okio.Buffer().also(::writeTo).readUtf8()
                }?.takeIf { it.isNotBlank() },
            ),
            PolymarketL2Header.POLY_TIMESTAMP to timestamp.toString(),
            PolymarketL2Header.POLY_API_KEY to credentials.apiKey,
            PolymarketL2Header.POLY_PASSPHRASE to credentials.passphrase,
        )

        return request.newBuilder()
            .apply { valueByHeader.forEach { addHeader(it.key.name, it.value) } }
            .build()
            .let(chain::proceed)
    }
}