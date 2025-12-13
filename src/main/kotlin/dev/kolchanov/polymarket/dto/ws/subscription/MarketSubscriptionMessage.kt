package dev.kolchanov.polymarket.dto.ws.subscription

import com.fasterxml.jackson.annotation.JsonProperty

data class MarketSubscriptionMessage(
    @field:JsonProperty("assets_ids")
    val assetsIds: List<String>,
    val type: String,
)