package dev.kolchanov.polymarket.dto.ws.subscription

data class WsLiveDataSubscriptionMessage(
    val action: String,
    val subscriptions: List<Subscription>
) {

    data class Subscription(
        val topic: String,
        val type: String,
        val filters: String? = null,
    )
}