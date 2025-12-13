package dev.kolchanov.polymarket.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class Outcome {
    UP,
    DOWN,
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun of(outcome: String): Outcome =
            valueOf(outcome.uppercase())
    }
}