package dev.kolchanov.polymarket.dto.ws.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 * Example Message:
 * {
 *   "connection_id": "UERwreZFrPECI6g="
 *   "topic": "crypto_prices_chainlink",
 *   "type": "update",
 *   "timestamp": 1753314064237,
 *   "payload": {
 *   full_accuracy_value: "96068873932830260000000"
 *     "symbol": "eth/usd",
 *     "timestamp": 1753314064213,
 *     "value": 3456.78
 *   }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChainlinkSourceMessageFormatResponse(
    @field:JsonProperty("connection_id")
    val connectionId: String,
    /** Topic name */
    val topic: String,
    /** Message type */
    val type: String,
    /** Message timestamp in Unix milliseconds */
    val timestamp: Long,
    /** Payload with price data */
    val payload: Payload,
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Payload(
        @field:JsonProperty("full_accuracy_value")
        val fullAccuracyValue: String,
        /** Trading pair symbol (Chainlink: slash-separated, e.g., "eth/usd", "btc/usd") */
        val symbol: String,
        /** Price timestamp in Unix milliseconds */
        val timestamp: Long,
        /** Current price value in the quote currency */
        val value: BigDecimal,
    )

    companion object {
        const val TOPIC_NAME = "crypto_prices_chainlink"
    }
}