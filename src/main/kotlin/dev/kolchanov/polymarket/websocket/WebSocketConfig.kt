package dev.kolchanov.polymarket.websocket

/**
 * Configuration for WebSocket connection and reconnect behavior.
 *
 * @property minBackoffMs Minimum backoff delay in milliseconds before reconnection attempt. Default: 2000ms.
 * @property maxBackoffMs Maximum backoff delay in milliseconds. Default: 60000ms (1 minute).
 * @property connectionLostTimeout Timeout in seconds after which connection is considered lost. Default: 30s.
 * @property jitterFactor Jitter factor for randomizing reconnect delay (0.0 to 1.0). Default: 0.2 (±20%).
 */
data class WebSocketConfig(
    val minBackoffMs: Long = 2_000L,
    val maxBackoffMs: Long = 60_000L,
    val connectionLostTimeout: Int = 30,
    val jitterFactor: Double = 0.2,
) {
    init {
        require(minBackoffMs > 0) { "minBackoffMs must be positive" }
        require(maxBackoffMs >= minBackoffMs) { "maxBackoffMs must be >= minBackoffMs" }
        require(connectionLostTimeout >= 0) { "connectionLostTimeout must be non-negative" }
        require(jitterFactor in 0.0..1.0) { "jitterFactor must be between 0.0 and 1.0" }
    }
}