package dev.kolchanov.polymarket.api.clob

import dev.kolchanov.polymarket.api.AbstractApi
import dev.kolchanov.polymarket.api.interceptor.AuthL2Interceptor
import dev.kolchanov.polymarket.dto.api.response.ApiKeysResponse
import dev.kolchanov.polymarket.dto.api.response.ClosedOnlyModeStatusResponse
import dev.kolchanov.polymarket.enums.system.SystemEnv
import kotlinx.coroutines.Deferred

/**
 * API client for L2 (API key-based) authentication with Polymarket CLOB.
 *
 * L2 authentication uses API key, secret, and passphrase credentials
 * for authenticating requests to Polymarket's CLOB (posting/canceling orders,
 * retrieving account orders and fills).
 *
 * Credentials are deterministically generated from wallet signature:
 * - **key**: UUID identifying the credentials
 * - **secret**: Secret string used to generate HMACs (not sent with requests)
 * - **passphrase**: Secret string sent with each request for encryption
 *
 * @param authL2Interceptor L2 authentication interceptor for signing requests.
 * @param baseUrl Base URL for the auth API. Default: from [SystemEnv.POLYMARKET_CLOB_URL].
 *
 * @see AuthL2Interceptor
 * @see ApiKeysResponse
 */
class AuthL2Api(
    authL2Interceptor: AuthL2Interceptor,
    baseUrl: String = "${SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault()}/auth",
) : AbstractApi(
    baseUrl = baseUrl,
    interceptors = listOf(authL2Interceptor),
) {
    /**
     * Retrieves all API keys associated with the authenticated Polygon address.
     *
     * @return [Deferred] with [ApiKeysResponse] containing list of API keys.
     */
    fun getApiKeys(): Deferred<ApiKeysResponse> =
        super.get(path = "/api-keys")

    /**
     * Deletes the API key used to authenticate this request.
     *
     * After deletion, the credentials can no longer be used for authentication.
     *
     * @return [Deferred] with deletion result.
     */
    fun deleteApiKey(): Deferred<Any?> =
        super.delete(path = "/api-key", body = null)

    /**
     * Retrieves the closed-only mode status for the authenticated address.
     *
     * Closed-only mode restricts the account to only closing existing positions.
     *
     * @return [Deferred] with [ClosedOnlyModeStatusResponse] containing status flag.
     */
    fun getClosedOnlyModeStatus(): Deferred<ClosedOnlyModeStatusResponse> =
        super.get(path = "/ban-status/closed-only")
}
