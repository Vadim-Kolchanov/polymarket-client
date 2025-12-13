package dev.kolchanov.polymarket.enums

enum class Fidelity(val value: String) {
    ONE_HOUR("1h"),
    ONE_DAY("1d"),
    ;

    override fun toString(): String {
        return value
    }
}