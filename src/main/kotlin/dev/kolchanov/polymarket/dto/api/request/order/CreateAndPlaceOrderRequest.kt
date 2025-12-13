package dev.kolchanov.polymarket.dto.api.request.order

import dev.kolchanov.polymarket.enums.OrderType
import dev.kolchanov.polymarket.enums.TradeSide

data class CreateAndPlaceOrderRequest(
    val deferExec: Boolean = false,
    /**
     * signed object
     */
    val order: Order,
    /**
     * api key of order owner
     */
    val owner: String,
    /**
     * order type (“FOK”, “GTC”, “GTD”)
     */
    val orderType: OrderType,
) {

    data class Order(
        /**
         * random salt used to create unique order
         */
        val salt: Long,
        /**
         * maker address (funder)
         */
        val maker: String,
        /**
         * signing address
         */
        val signer: String,
        /**
         * taker address (operator)
         */
        val taker: String = "0x0000000000000000000000000000000000000000",
        /**
         * AssetId
         * ERC1155 token ID of conditional token being traded
         */
        val tokenId: String,
        /**
         * maximum amount maker is willing to spend
         */
        val makerAmount: String,
        /**
         * minimum amount taker will pay the maker in return
         */
        val takerAmount: String,
        /**
         * unix expiration timestamp
         */
        val expiration: String,
        /**
         * maker’s exchange nonce of the order is associated
         */
        val nonce: String,
        /**
         * fee rate basis points as required by the operator
         */
        val feeRateBps: String = "0",
        /**
         * buy or sell enum index
         */
        val side: TradeSide,
        /**
         * signature type enum index
         */
        val signatureType: Int,
        /**
         * hex encoded signature
         */
        var signature: String = "",
    )
}
