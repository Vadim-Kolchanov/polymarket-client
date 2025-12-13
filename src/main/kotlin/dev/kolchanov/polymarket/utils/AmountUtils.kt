package dev.kolchanov.polymarket.utils

import java.math.BigDecimal
import java.math.RoundingMode

object AmountUtils {

    private const val COLLATERAL_TOKEN_DECIMALS = 6

    /**
     * Converts a decimal amount to its integer representation based on token decimals
     * Similar to ethers.js parseUnits
     *
     * @param value The decimal value as string (e.g., "0.46")
     * @param decimals Number of decimals for the token (default 6 for USDC)
     * @return The scaled integer value as string
     */
    fun parseUnits(amount: BigDecimal, decimals: Int = COLLATERAL_TOKEN_DECIMALS): String {
        val scale = BigDecimal.TEN.pow(decimals)

        return amount.multiply(scale)
            .setScale(0, RoundingMode.DOWN)
            .toBigInteger()
            .toString()
    }

    /**
     * Converts an integer amount back to decimal representation
     * Similar to ethers.js formatUnits
     *
     * @param value The integer value as string
     * @param decimals Number of decimals for the token (default 6 for USDC)
     * @return The decimal value as string
     */
    fun formatUnits(value: String, decimals: Int = COLLATERAL_TOKEN_DECIMALS): String {
        val amount = BigDecimal(value)
        val scale = BigDecimal.TEN.pow(decimals)

        return amount.divide(scale, decimals, RoundingMode.DOWN)
            .stripTrailingZeros()
            .toPlainString()
    }
}
