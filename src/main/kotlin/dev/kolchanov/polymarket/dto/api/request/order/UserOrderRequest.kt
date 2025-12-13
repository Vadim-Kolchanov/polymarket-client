package dev.kolchanov.polymarket.dto.api.request.order

import dev.kolchanov.polymarket.dto.PolymarketContext
import dev.kolchanov.polymarket.enums.OrderType
import dev.kolchanov.polymarket.enums.TradeSide
import dev.kolchanov.polymarket.utils.AmountUtils
import dev.kolchanov.polymarket.utils.Eip712Signer
import dev.kolchanov.polymarket.utils.OrderUtils
import java.math.BigDecimal

/**
 * User-friendly order request DTO for placing orders on Polymarket.
 *
 * Simplified representation of an order that can be converted to a full
 * [CreateAndPlaceOrderRequest] with proper signing and amounts calculation.
 *
 * @property assetId Token ID of the asset to trade (condition token).
 * @property side Trade side: [TradeSide.BUY] or [TradeSide.SELL].
 * @property price Order price (0.01 to 1.0 range for binary markets).
 * @property size Order size in shares.
 * @property orderType Order type. Default: [OrderType.GTC] (Good Till Cancelled).
 * @property expiration Order expiration timestamp. Default: "0" (no expiration).
 *
 * @see CreateAndPlaceOrderRequest
 * @see TradeSide
 * @see OrderType
 */
data class UserOrderRequest(
    val assetId: String,
    val side: TradeSide,
    val price: BigDecimal,
    val size: BigDecimal,
    val orderType: OrderType = OrderType.GTC,
    val expiration: String = "0",
) {

    init {
        require(price >= BigDecimal("0.01") && price <= BigDecimal("1.0")) {
            "Price must be in range 0.01 to 1.0, but was $price"
        }
        require(size > BigDecimal.ZERO) { "Size must be positive, but was $size" }
    }

    /**
     * Converts this user order to a signed [CreateAndPlaceOrderRequest].
     *
     * Calculates maker/taker amounts based on side, size, and price,
     * then signs the order using EIP-712 typed data signature.
     *
     * @param owner API key (owner address) for the order.
     * @param context Polymarket context with wallet credentials and chain settings.
     * @return Signed [CreateAndPlaceOrderRequest] ready to be sent to the API.
     *
     * @see Eip712Signer.signClobPolymarketOrderTypedData
     * @see OrderUtils.getOrderRawAmounts
     */
    fun toCreateAndPlaceOrderRequest(
        owner: String,
        context: PolymarketContext,
    ): CreateAndPlaceOrderRequest = context.run {
        require(owner.isNotBlank()) { "Owner must not be blank" }
        require(address.isNotBlank()) { "Address must not be blank" }
        require(privateKey.isNotBlank()) { "Private key must not be blank" }
        require(funderAddress.isNotBlank()) { "Funder Address must not be blank" }

        val amounts = OrderUtils.getOrderRawAmounts(side = side, size = size, price = price)

        return CreateAndPlaceOrderRequest(
            owner = owner,
            orderType = orderType,
            order = CreateAndPlaceOrderRequest.Order(
                salt = OrderUtils.generateOrderSalt(),
                maker = funderAddress,
                signer = address,
                tokenId = assetId,
                makerAmount = AmountUtils.parseUnits(amounts.rawMakerAmt),
                takerAmount = AmountUtils.parseUnits(amounts.rawTakerAmt),
                expiration = expiration,
                nonce = nonce.toString(),
                side = side,
                signatureType = signatureType.index,
            ),
        ).apply {
            this.order.signature = Eip712Signer.signClobPolymarketOrderTypedData(
                chainId = chainId,
                privateKey = privateKey,
                order = this.order,
            )
        }
    }
}
