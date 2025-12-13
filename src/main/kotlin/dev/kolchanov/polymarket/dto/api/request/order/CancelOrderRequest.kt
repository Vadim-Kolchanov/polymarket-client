package dev.kolchanov.polymarket.dto.api.request.order

import com.fasterxml.jackson.annotation.JsonProperty

data class CancelOrderRequest(
    @field:JsonProperty("orderID")
    val orderId: String,
)
