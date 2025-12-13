package dev.kolchanov.polymarket.api.gamma

import dev.kolchanov.polymarket.api.AbstractApi
import dev.kolchanov.polymarket.dto.api.response.gamma.MarketResponse
import dev.kolchanov.polymarket.enums.system.SystemEnv
import kotlinx.coroutines.Deferred

/**
 * API client for accessing Polymarket Gamma markets data.
 *
 * Provides methods for fetching market information from the Gamma API.
 * This is a public API that does not require authentication.
 *
 * @param baseUrl Base URL for the markets API. Default: from [SystemEnv.GAMMA_API_POLYMARKET_URL].
 */
class MarketsApi(
    baseUrl: String = "${SystemEnv.GAMMA_API_POLYMARKET_URL.getEnvOrDefault()}/markets",
) : AbstractApi(baseUrl = baseUrl) {

    /**
     * Fetches market data by its slug identifier.
     *
     * @param slug Unique slug identifier of the market (e.g., "what-price-will-bitcoin-hit-in-2025").
     * @return [Deferred] with [MarketResponse] containing market details.
     */
    fun getMarketBySlug(slug: String): Deferred<MarketResponse> =
        super.get(path = "/slug/$slug")
}