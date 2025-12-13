package dev.kolchanov.polymarket.websocket.impl

import dev.kolchanov.polymarket.dto.ws.subscription.UserSubscriptionMessage
import dev.kolchanov.polymarket.enums.system.SystemEnv
import dev.kolchanov.polymarket.websocket.AbstractWebSocket

class UserPolymarketWebSocket(
    webSocketUrl: String = SystemEnv.WS_POLYMARKET_USER_URL.getEnvOrDefault(),
    onMessageHandle: (message: String) -> Unit,
    private val auth: UserSubscriptionMessage.Auth,
    private val marketConditionIds: List<String>,
) : AbstractWebSocket<UserSubscriptionMessage>(
    webSocketUrl = webSocketUrl,
    onMessageHandle = onMessageHandle,
) {

    override fun getSubscriptionMessage(): UserSubscriptionMessage =
        UserSubscriptionMessage(
            auth = auth,
            markets = marketConditionIds,
            type = TYPE_MARKET,
        )

    companion object {
        const val TYPE_MARKET = "user"
    }
}