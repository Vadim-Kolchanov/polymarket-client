package dev.kolchanov.polymarket.dto.api.response.gamma

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * Gamma market description.
 * ``` json
 * {
 *  "id": "683595",
 *  "question": "Bitcoin Up or Down - November 17, 12AM ET",
 *  "conditionId": "0x1a7999c71f8b875bdc903734a2107fbcf8d7dfdcbc9d3c51731a8c1460e11898",
 *  "slug": "bitcoin-up-or-down-november-17-12am-et",
 *  "endDate": "2025-11-17T06:00:00Z",
 *  "startDate": "2025-11-15T05:00:57.755897Z",
 *  "outcomes": "[\"Up\", \"Down\"]",
 *  "outcomePrices": "[\"0.5\", \"0.5\"]",
 *  "active": true,
 *  "closed": false,
 *  "enableOrderBook": true,
 *  "orderPriceMinTickSize": 0.01,
 *  "orderMinSize": 5,
 *  "clobTokenIds": "[\"97633282141727012715463055428185899320650283789416612830799232303088670062150\", \"51083518851574675893213781153121136313356956848703961741777739233450403802026\"]",
 *  "negRisk": false,
 *  "acceptingOrdersTimestamp": "2025-11-15T05:00:36Z"
 *  "eventStartTime": "2025-11-15T10:15:00Z",
 * }
 * ```
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MarketResponse(
    /** Unique market identifier (string form). */
    val id: String,
    /** Human-readable question describing the market. */
    val question: String,
    /** Condition (smart contract) identifier (hex string). */
    val conditionId: String,
    /** SEO-friendly slug. */
    val slug: String,
    val eventStartTime: OffsetDateTime,
    /** Market end date/time (ISO-8601, UTC). */
    val endDate: OffsetDateTime,
    /** Market start date/time (ISO-8601, UTC). */
    val startDate: String?,
    /** Raw JSON-like string with outcome labels. */
    val outcomes: String,
    /** Raw JSON-like string with outcome price strings. */
    val outcomePrices: String,
    /** Whether market is currently active. */
    val active: Boolean,
    /** Whether market is closed for trading. */
    val closed: Boolean,
    /** Whether on-chain CLOB order book is enabled. */
    val enableOrderBook: Boolean?,
    /** Minimum tick size for order prices. */
    val orderPriceMinTickSize: BigDecimal?,
    /** Minimum order size (units). */
    val orderMinSize: Int?,
    /** Raw JSON-like string with CLOB token identifiers. */
    val clobTokenIds: String,
    /** Negative risk flag (whether neg-risk structure applies). */
    val negRisk: Boolean?,
    /** When market started accepting orders (ISO-8601, UTC). */
    val acceptingOrdersTimestamp: String?,
)
