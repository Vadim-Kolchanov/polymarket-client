package dev.kolchanov.polymarket.enums.system

enum class SystemEnv(val default: String = "") {
    POLYMARKET_CLOB_URL(default = "https://clob.polymarket.com"),
    POLYMARKET_API_URL(default = "https://polymarket.com/api"),
    GAMMA_API_POLYMARKET_URL(default = "https://gamma-api.polymarket.com"),
    DATA_API_POLYMARKET_URL(default = "https://data-api.polymarket.com"),

    // WebSocket URLs
    WS_POLYMARKET_MARKET_URL(default = "wss://ws-subscriptions-clob.polymarket.com/ws/market"),
    WS_POLYMARKET_USER_URL(default = "wss://ws-subscriptions-clob.polymarket.com/ws/user"),
    WS_POLYMARKET_WS_LIVE_DATA_URL(default = "wss://ws-live-data.polymarket.com"),

    CHAIN_ID("137"),
    POLYGON_ADDRESS_WALLET,
    PRIVATE_KEY_WALLET,
    FUNDER_ADDRESS,
    ;

    fun getEnvOrDefault(): String =
        System.getenv(this.name) ?: this.default
}