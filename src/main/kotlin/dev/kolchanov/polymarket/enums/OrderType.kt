package dev.kolchanov.polymarket.enums

enum class OrderType {
    /**
     * A Fill-Or-Kill order is an market order to buy (in dollars) or sell (in shares) shares
     * that must be executed immediately in its entirety;
     * otherwise, the entire order will be cancelled.
     */
    FOK,

    /**
     * A Good-Til-Cancelled order is a limit order that is active until it is fulfilled or cancelled.
     */
    GTC,

    /**
     * A Good-Til-Date order is a type of order that is active until its specified date (UTC seconds timestamp),
     * unless it has already been fulfilled or cancelled.
     * There is a security threshold of one minute.
     * If the order needs to expire in 90 seconds the correct expiration value is: now + 1 minute + 30 seconds
     */
    GTD,
}