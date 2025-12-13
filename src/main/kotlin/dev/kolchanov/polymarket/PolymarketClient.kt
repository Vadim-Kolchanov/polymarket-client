package dev.kolchanov.polymarket

import dev.kolchanov.polymarket.api.clob.AuthL1Api
import dev.kolchanov.polymarket.api.clob.AuthL2Api
import dev.kolchanov.polymarket.api.clob.OrderApi
import dev.kolchanov.polymarket.api.gamma.MarketsApi
import dev.kolchanov.polymarket.api.interceptor.AuthL2Interceptor
import dev.kolchanov.polymarket.dto.PolymarketContext
import dev.kolchanov.polymarket.dto.api.response.PolymarketCredentials
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

/**
 * Main entry point for interacting with the Polymarket CLOB (Central Limit Order Book) API.
 *
 * Provides access to both authenticated (private) and public APIs for trading on Polymarket.
 *
 * ``` kotlin
 * val client = PolymarketClient(
 *     PolymarketContext(
 *         privateKey = "your-private-key",
 *         chain = 137, // Polygon
 *     )
 * )
 * // Access public markets
 * val markets = client.publicApi.marketsApi.getMarkets()
 * // Place orders (requires authentication)
 * client.privateApi.orderApi.placeOrder(order)
 * ```
 *
 * @param context Configuration context containing wallet credentials, API endpoints, and chain settings.
 * @see PolymarketContext
 */
class PolymarketClient(private val context: PolymarketContext = PolymarketContext()) {
    private val log = KotlinLogging.logger {}

    private val authL1Interceptor by lazy { context.toAuthL1Interceptor() }
    private val authL2Interceptor by lazy {
        AuthL2Interceptor(
            address = context.address,
            credentials = credentials,
        )
    }

    /**
     * APIs that require authentication (L1 and L2).
     *
     * Includes:
     * - [PrivateApi.authL1Api] - L1 authentication for API key management
     * - [PrivateApi.authL2Api] - L2 authentication for trading operations
     * - [PrivateApi.orderApi] - Order placement and management
     */
    val privateApi: PrivateApi by lazy { PrivateApi() }

    /**
     * APIs that do not require authentication.
     *
     * Includes:
     * - [PublicApi.marketsApi] - Market data and information
     */
    val publicApi: PublicApi by lazy { PublicApi() }

    /**
     * API credentials obtained from Polymarket.
     *
     * Lazily initialized by attempting to derive existing API keys,
     * or creating new ones if derivation fails.
     *
     * @see PolymarketCredentials
     */
    val credentials: PolymarketCredentials by lazy {
        runBlocking {
            runCatching { privateApi.authL1Api.deriveApiKey().await() }
                .onFailure { log.warn("Failed to derive api key. Try to create api keys...", it) }
                .getOrElse { privateApi.authL1Api.createApiKey().await() }
        }
    }

    /**
     * Container for authenticated API endpoints.
     *
     * All APIs in this class require valid wallet credentials in [PolymarketContext].
     */
    inner class PrivateApi() {
        /**
         * L1 Authentication API for managing API keys.
         *
         * Used for creating and deriving API credentials using wallet signature.
         */
        val authL1Api: AuthL1Api = AuthL1Api(authL1Interceptor = authL1Interceptor)

        /**
         * L2 Authentication API for trading-related authentication.
         *
         * Used for operations that require L2 (API key) authentication.
         */
        val authL2Api: AuthL2Api by lazy {
            AuthL2Api(authL2Interceptor = authL2Interceptor)
        }

        /**
         * Order API for placing and managing orders.
         *
         * Provides functionality to create, place, and cancel orders on Polymarket.
         */
        val orderApi: OrderApi by lazy {
            OrderApi(
                context = context,
                owner = credentials.apiKey,
                authL2Interceptor = authL2Interceptor,
            )
        }
    }

    /**
     * Container for public (unauthenticated) API endpoints.
     */
    inner class PublicApi() {
        /**
         * Markets API for fetching market data.
         *
         * Provides access to market information, prices, and other public data.
         */
        val marketsApi: MarketsApi = MarketsApi()
    }
}
