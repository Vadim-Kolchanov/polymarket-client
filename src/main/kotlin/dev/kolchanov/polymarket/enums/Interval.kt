package dev.kolchanov.polymarket.enums

enum class Interval(val value: String) {
    ALL("all"),
    ONE_MONTH("1m"),
    ONE_WEEK("1w"),
    ONE_DAY("1d"),
    ;

    override fun toString(): String {
        return value
    }
}