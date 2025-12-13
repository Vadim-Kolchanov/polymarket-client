package dev.kolchanov.polymarket.utils

import dev.kolchanov.polymarket.enums.TradeSide
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue
import kotlin.random.Random

object OrderUtils {

    data class OrderRawAmounts(
        val rawMakerAmt: BigDecimal,
        val rawTakerAmt: BigDecimal
    )

    /**
     * For tick size 0.01
     */
    private object RoundConfig {
        const val price = 2
        const val size = 2
        const val amount = 4
    }

    /**
     * Generates a unique salt for order creation
     * Must be a positive long value to match EIP712 uint256 type
     */
    fun generateOrderSalt(): Long =
        Random.nextLong(
            from = System.currentTimeMillis(),
            until = Long.MAX_VALUE,
        ).absoluteValue

    fun getOrderRawAmounts(
        side: TradeSide,
        size: BigDecimal,
        price: BigDecimal,
    ): OrderRawAmounts {
        val rawPrice = roundNormal(price, RoundConfig.price)

        return when (side) {
            TradeSide.BUY -> {
                val rawTakerAmt = roundDown(size, RoundConfig.size)
                var rawMakerAmt = rawTakerAmt.multiply(rawPrice)

                if (decimalPlaces(rawMakerAmt) > RoundConfig.amount) {
                    rawMakerAmt = roundUp(rawMakerAmt, RoundConfig.amount + 4)
                    if (decimalPlaces(rawMakerAmt) > RoundConfig.amount) {
                        rawMakerAmt = roundDown(rawMakerAmt, RoundConfig.amount)
                    }
                }

                OrderRawAmounts(
                    rawMakerAmt = rawMakerAmt,
                    rawTakerAmt = rawTakerAmt,
                )
            }

            TradeSide.SELL -> {
                val rawMakerAmt = roundDown(size, RoundConfig.size)
                var rawTakerAmt = rawMakerAmt.multiply(rawPrice)

                if (decimalPlaces(rawTakerAmt) > RoundConfig.amount) {
                    rawTakerAmt = roundUp(rawTakerAmt, RoundConfig.amount + 4)
                    if (decimalPlaces(rawTakerAmt) > RoundConfig.amount) {
                        rawTakerAmt = roundDown(rawTakerAmt, RoundConfig.amount)
                    }
                }

                OrderRawAmounts(
                    rawMakerAmt = rawMakerAmt,
                    rawTakerAmt = rawTakerAmt,
                )
            }
        }
    }

    private fun roundDown(value: BigDecimal, decimals: Int): BigDecimal =
        value.setScale(decimals, RoundingMode.DOWN)

    private fun roundUp(value: BigDecimal, decimals: Int): BigDecimal =
        value.setScale(decimals, RoundingMode.UP)

    private fun roundNormal(value: BigDecimal, decimals: Int): BigDecimal =
        value.setScale(decimals, RoundingMode.HALF_UP)

    private fun decimalPlaces(value: BigDecimal): Int =
        value.stripTrailingZeros().scale().coerceAtLeast(0)
}
