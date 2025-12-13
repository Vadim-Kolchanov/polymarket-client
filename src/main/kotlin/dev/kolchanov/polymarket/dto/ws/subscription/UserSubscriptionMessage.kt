package dev.kolchanov.polymarket.dto.ws.subscription

data class UserSubscriptionMessage(
    val auth: Auth,
    /**
     * conditionId of market to subscribe to
     */
    val markets: List<String>,
    val type: String,
) {

    data class Auth(
        val apiKey: String,
        val secret: String,
        val passphrase: String,
    )
}