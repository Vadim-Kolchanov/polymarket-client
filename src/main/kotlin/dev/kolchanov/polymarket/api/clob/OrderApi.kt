package dev.kolchanov.polymarket.api.clob

import dev.kolchanov.polymarket.api.AbstractApi
import dev.kolchanov.polymarket.api.interceptor.AuthL2Interceptor
import dev.kolchanov.polymarket.dto.PolymarketContext
import dev.kolchanov.polymarket.dto.api.request.order.CancelOrderRequest
import dev.kolchanov.polymarket.dto.api.request.order.UserOrderRequest
import dev.kolchanov.polymarket.dto.api.response.order.CancelOrderResponse
import dev.kolchanov.polymarket.dto.api.response.order.CreateAndPlaceOrderResponse
import dev.kolchanov.polymarket.enums.system.SystemEnv
import kotlinx.coroutines.Deferred

/**
 * API client for order management on Polymarket CLOB.
 *
 * Provides methods for creating, placing, and canceling orders.
 * Requires L2 (API key-based) authentication.
 *
 * @param context Polymarket configuration context with wallet and chain settings.
 * @param owner API key (owner address) for order signing.
 * @param authL2Interceptor L2 authentication interceptor.
 * @param baseUrl Base URL for the order API. Default: from [SystemEnv.POLYMARKET_CLOB_URL].
 *
 * @see AuthL2Interceptor
 * @see UserOrderRequest
 */
class OrderApi(
    val context: PolymarketContext,
    val owner: String,
    authL2Interceptor: AuthL2Interceptor,
    baseUrl: String = "${SystemEnv.POLYMARKET_CLOB_URL.getEnvOrDefault()}/order",
) : AbstractApi(
    baseUrl = baseUrl,
    interceptors = listOf(authL2Interceptor),
) {

    /**
     * Creates and places an order on the Polymarket CLOB.
     *
     * @param userOrder User order request with order parameters.
     * @return [Deferred] with [CreateAndPlaceOrderResponse] containing order result.
     */
    fun createAndPlaceOrder(userOrder: UserOrderRequest): Deferred<CreateAndPlaceOrderResponse> =
        super.post(path = EMPTY_PATH, body = userOrder.toCreateAndPlaceOrderRequest(owner = owner, context = context))

    /**
     * Cancels an existing order by its ID.
     *
     * @param orderId Unique identifier of the order to cancel.
     * @return [Deferred] with [CancelOrderResponse] containing cancellation result.
     */
    fun cancelOrder(orderId: String): Deferred<CancelOrderResponse> =
        super.delete(path = EMPTY_PATH, body = CancelOrderRequest(orderId = orderId))
}
