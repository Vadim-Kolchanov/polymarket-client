package dev.kolchanov.polymarket.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.kolchanov.polymarket.websocket.WebSocketConfig
import mu.KotlinLogging
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Abstract WebSocket client with automatic reconnection support.
 *
 * @param SM Type of the subscription message sent on connection open.
 * @param webSocketUrl WebSocket server URL.
 * @param onMessageHandle Callback invoked when a message is received.
 * @param config Configuration for reconnect behavior.
 */
abstract class AbstractWebSocket<SM>(
    private val webSocketUrl: String,
    private val onMessageHandle: (message: String) -> Unit,
    private val config: WebSocketConfig = WebSocketConfig(),
) : WebSocketClient(
    URI(webSocketUrl),
    Draft_6455(),
) {
    private val log = KotlinLogging.logger {}
    private val objectMapper = jacksonObjectMapper()

    // reconnect control
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val isReconnecting = AtomicBoolean(false)
    private val isShutdown = AtomicBoolean(false)
    private var backoffMs = config.minBackoffMs

    abstract fun getSubscriptionMessage(): SM

    override fun onOpen(handshakedata: ServerHandshake?) {
        log.info { "$webSocketUrl connection opened" }

        isReconnecting.set(false)
        backoffMs = config.minBackoffMs
        connectionLostTimeout = config.connectionLostTimeout

        val json = objectMapper.writeValueAsString(getSubscriptionMessage())
        super.send(json)
    }

    override fun onMessage(message: String) {
        runCatching { onMessageHandle(message) }
            .onFailure { ex -> log.warn(ex) { "$webSocketUrl: onMessage handler failed" } }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        log.info { "$webSocketUrl connection closed: code=$code, remote=$remote, reason=${reason ?: ""}" }
        if (!isShutdown.get()) scheduleReconnect()
    }

    override fun onError(ex: Exception) {
        log.error(ex) { "$webSocketUrl error" }
        if (!isShutdown.get() && !isOpen && !isReconnecting.get()) {
            scheduleReconnect()
        }
    }

    fun shutdown() {
        if (!isShutdown.compareAndSet(false, true)) return
        try {
            connectionLostTimeout = 0
        } catch (_: Exception) {
        }
        runCatching { close() }
        scheduler.shutdownNow()
    }

    private fun scheduleReconnect() {
        if (isShutdown.get()) return
        if (!isReconnecting.compareAndSet(false, true)) return

        // jitter ± jitterFactor%
        val jitter = (backoffMs * config.jitterFactor).toLong()
        val delay = max(0L, backoffMs + Random.nextLong(-jitter, jitter + 1))
        log.info { "$webSocketUrl attempting reconnect in ${delay}ms..." }

        scheduler.schedule({
            try {
                // Block inside scheduler thread, never in WS callback thread
                reconnectBlocking()
                log.info { "$webSocketUrl reconnected" }
            } catch (e: Exception) {
                log.error(e) { "$webSocketUrl reconnect failed" }
                isReconnecting.set(false)
                backoffMs = min(config.maxBackoffMs, max(config.minBackoffMs, backoffMs * 2))
                scheduleReconnect()
            } finally {
                // allow next reconnect attempt to be scheduled if still not open
                if (!isOpen) isReconnecting.set(false)
            }
        }, delay, TimeUnit.MILLISECONDS)
    }
}