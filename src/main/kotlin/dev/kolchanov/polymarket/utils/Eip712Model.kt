package dev.kolchanov.polymarket.utils

import com.fasterxml.jackson.annotation.JsonProperty
import dev.kolchanov.polymarket.enums.MaticContract

object Eip712Model {

    const val NONCE = 0L
    const val EIP712_DOMAIN = "EIP712Domain"
    const val AUTH_PRIMARY_TYPE = "ClobAuth"
    const val ORDER_PRIMARY_TYPE = "Order"

    data class Type(
        val name: String,
        val type: String,
    ) {
        companion object {
            const val STRING = "string"
            const val UINT256 = "uint256"
            const val UINT8 = "uint8"
            const val ADDRESS = "address"
        }
    }

    data class PolymarketClobAuth(
        val domain: Domain,
        val types: Types = Types(),
        val primaryType: String = AUTH_PRIMARY_TYPE,
        val message: Message,
    ) {
        companion object {
            fun of(chainId: Long, address: String, timestamp: Long): PolymarketClobAuth =
                PolymarketClobAuth(
                    domain = Domain(
                        chainId = chainId,
                    ),
                    message = Message(
                        address = address,
                        timestamp = timestamp.toString(),
                    ),
                )
        }

        data class Domain(
            val name: String = "ClobAuthDomain",
            val version: String = "1",
            val chainId: Long,
        )

        data class Types(
            @field:JsonProperty(EIP712_DOMAIN)
            val EIP712Domain: List<Type> = listOf(
                Type(name = "name", type = Type.STRING),
                Type(name = "version", type = Type.STRING),
                Type(name = "chainId", type = Type.UINT256),
            ),
            @field:JsonProperty(AUTH_PRIMARY_TYPE)
            val ClobAuth: List<Type> = listOf(
                Type(name = "address", type = Type.ADDRESS),
                Type(name = "timestamp", type = Type.STRING),
                Type(name = "nonce", type = Type.UINT256),
                Type(name = "message", type = Type.STRING),
            ),
        )

        data class Message(
            val address: String,
            val timestamp: String,
            val nonce: Long = NONCE,
            val message: String = "This message attests that I control the given wallet",
        )
    }

    data class PolymarketClobOrder(
        val primaryType: String = ORDER_PRIMARY_TYPE,
        val domain: Domain,
        val types: Types = Types(),
        val message: OrderMessage,
    ) {
        companion object {
            fun of(chainId: Long, order: OrderMessage): PolymarketClobOrder =
                PolymarketClobOrder(
                    domain = Domain(
                        chainId = chainId,
                    ),
                    message = order,
                )
        }

        data class Domain(
            val name: String = "Polymarket CTF Exchange",
            val version: String = "1",
            val chainId: Long,
            val verifyingContract: String = MaticContract.EXCHANGE.address,
        )

        data class Types(
            @field:JsonProperty(EIP712_DOMAIN)
            val EIP712Domain: List<Type> = listOf(
                Type(name = "name", type = Type.STRING),
                Type(name = "version", type = Type.STRING),
                Type(name = "chainId", type = Type.UINT256),
                Type(name = "verifyingContract", type = Type.ADDRESS),
            ),
            @field:JsonProperty(ORDER_PRIMARY_TYPE)
            val Order: List<Type> = listOf(
                Type(name = "salt", type = Type.UINT256),
                Type(name = "maker", type = Type.ADDRESS),
                Type(name = "signer", type = Type.ADDRESS),
                Type(name = "taker", type = Type.ADDRESS),
                Type(name = "tokenId", type = Type.UINT256),
                Type(name = "makerAmount", type = Type.UINT256),
                Type(name = "takerAmount", type = Type.UINT256),
                Type(name = "expiration", type = Type.UINT256),
                Type(name = "nonce", type = Type.UINT256),
                Type(name = "feeRateBps", type = Type.UINT256),
                Type(name = "side", type = Type.UINT8),
                Type(name = "signatureType", type = Type.UINT8),
            ),
        )

        data class OrderMessage(
            val salt: String,
            val maker: String,
            val signer: String,
            val taker: String,
            val tokenId: String,
            val makerAmount: String,
            val takerAmount: String,
            val expiration: String,
            val nonce: String,
            val feeRateBps: String,
            val side: String,
            val signatureType: String,
        )
    }
}
