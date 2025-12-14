package dev.kolchanov.polymarket.dto.ws.response.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 * Example Message:
 * ``` json
 * {
 *   "market": "0x0270a8ce890ade23ace25488e1e00480189373e7fee45a6ae80f3cae9bd01e85",
 *   "price_changes": [
 *     {
 *       "asset_id": "100257694793825545690880780160963130081516494584156260920620231154143979714267",
 *       "price": "0.9",
 *       "size": "105",
 *       "side": "BUY",
 *       "hash": "1abe6e3c87e3517f0c3ea89270a5edf0b4d7515c",
 *       "best_bid": "0.9",
 *       "best_ask": "0.93"
 *     }
 *   ],
 *   "timestamp": "1763200441958",
 *   "event_type": "price_change"
 * }
 * ```
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PriceChangeEventMessageResponse(
    eventType: String,

    /** Market id (hex). */
    val market: String,
    /** List of individual price change entries. */
    @field:JsonProperty("price_changes")
    val priceChanges: List<PriceChange>,
    /** Event timestamp in Unix millis. */
    val timestamp: Long,
) : EventMessageResponse(eventType) {

    companion object {
        const val EVENT_TYPE: String = "price_change"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PriceChange(
        /** Asset id (large integer as string). */
        @field:JsonProperty("asset_id")
        val assetId: String,
        /** Trade or quote price. */
        val price: BigDecimal,
        /** Executed size (quantity). */
        val size: String,
        /** Side of the change: BUY or SELL. */
        val side: String,
        /** Transaction / update hash. */
        val hash: String,
        /** Current best bid after the update. */
        @field:JsonProperty("best_bid")
        val bestBid: BigDecimal,
        /** Current best ask after the update. */
        @field:JsonProperty("best_ask")
        val bestAsk: BigDecimal
    )
}

