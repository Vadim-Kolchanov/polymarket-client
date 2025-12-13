package dev.kolchanov.polymarket.websocket.impl

import dev.kolchanov.polymarket.dto.ws.subscription.WsLiveDataSubscriptionMessage
import dev.kolchanov.polymarket.enums.system.SystemEnv
import dev.kolchanov.polymarket.websocket.AbstractWebSocket

class WsLiveDataPolymarketWebSocketClient(
    webSocketUrl: String = SystemEnv.WS_POLYMARKET_WS_LIVE_DATA_URL.getEnvOrDefault(),
    onMessageHandle: (message: String) -> Unit,
    private val subscriptions: List<WsLiveDataSubscriptionMessage.Subscription>
) : AbstractWebSocket<WsLiveDataSubscriptionMessage>(
    webSocketUrl = webSocketUrl,
    onMessageHandle = onMessageHandle,
) {

    override fun getSubscriptionMessage(): WsLiveDataSubscriptionMessage =
        WsLiveDataSubscriptionMessage(
            action = "subscribe",
            subscriptions = subscriptions,
        )
}