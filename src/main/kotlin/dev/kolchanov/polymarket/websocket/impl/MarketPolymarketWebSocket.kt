package dev.kolchanov.polymarket.websocket.impl

import dev.kolchanov.polymarket.dto.ws.subscription.MarketSubscriptionMessage
import dev.kolchanov.polymarket.enums.system.SystemEnv
import dev.kolchanov.polymarket.websocket.AbstractWebSocket

class MarketPolymarketWebSocket(
    webSocketUrl: String = SystemEnv.WS_POLYMARKET_MARKET_URL.getEnvOrDefault(),
    onMessageHandle: (message: String) -> Unit,
    private val assetsIds: List<String>,
) : AbstractWebSocket<MarketSubscriptionMessage>(
    webSocketUrl = webSocketUrl,
    onMessageHandle = onMessageHandle,
) {

    override fun getSubscriptionMessage(): MarketSubscriptionMessage =
        MarketSubscriptionMessage(
            assetsIds = assetsIds,
            type = TYPE_MARKET,
        )

    companion object {
        const val TYPE_MARKET = "market"
    }
}