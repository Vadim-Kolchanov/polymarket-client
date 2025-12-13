package dev.kolchanov.polymarket.api.clob

import dev.kolchanov.polymarket.api.AbstractApi
import dev.kolchanov.polymarket.api.interceptor.AuthL1Interceptor
import dev.kolchanov.polymarket.dto.api.response.PolymarketCredentials
import dev.kolchanov.polymarket.enums.system.SystemEnv
import kotlinx.coroutines.Deferred

/**
 * API client for L1 (wallet-based) authentication with Polymarket CLOB.
 *
 * Provides methods for creating and deriving API key credentials
 * using wallet signature authentication.
 *
 * @param authL1Interceptor L1 authentication interceptor for signing requests.
 * @param baseUrl Base URL for the auth API. Default: from [SystemEnv.POLYMARKET_CLOB_URL].
 *
 * @see AuthL1Interceptor
 * @see PolymarketCredentials
 */
class AuthL1Api(
    authL1Interceptor: AuthL1Interceptor,
    baseUrl: String = "${SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault()}/auth",
) : AbstractApi(
    baseUrl = baseUrl,
    interceptors = listOf(authL1Interceptor),
) {
    /**
     * Creates new API key credentials for a user.
     *
     * Generates a new set of credentials (apiKey, secret, passphrase)
     * for the authenticated wallet address.
     *
     * @return [Deferred] with [PolymarketCredentials] containing new API credentials.
     */
    fun createApiKey(): Deferred<PolymarketCredentials> =
        super.post(path = "/api-key", body = null)

    /**
     * Derives an existing API key for the authenticated address and nonce.
     *
     * Retrieves previously created credentials without generating new ones.
     * Useful for recovering credentials on a new device.
     *
     * @return [Deferred] with [PolymarketCredentials] containing existing API credentials.
     */
    fun deriveApiKey(): Deferred<PolymarketCredentials> =
        super.get(path = "/derive-api-key")
}
