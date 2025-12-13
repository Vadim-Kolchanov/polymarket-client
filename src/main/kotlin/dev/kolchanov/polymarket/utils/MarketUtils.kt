package dev.kolchanov.polymarket.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset

object MarketUtils {

    private const val INTERVAL_MINUTES_15 = 15

    fun build15MinMarketSlug(
        now: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
        symbol: String = "btc",
        type: String = "updown",
    ): String {
        val rounded = now
            .withMinute((now.minute / INTERVAL_MINUTES_15) * INTERVAL_MINUTES_15)
            .withSecond(0)
            .withNano(0)

        return "$symbol-$type-${INTERVAL_MINUTES_15}m-${rounded.toEpochSecond()}"
    }
}
