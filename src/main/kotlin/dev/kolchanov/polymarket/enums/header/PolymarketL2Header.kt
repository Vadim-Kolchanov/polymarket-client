package dev.kolchanov.polymarket.enums.header

enum class PolymarketL2Header {
    /**
     * Polygon address
     */
    POLY_ADDRESS,

    /**
     * HMAC signature for request
     */
    POLY_SIGNATURE,

    /**
     * Current UNIX timestamp in seconds
     */
    POLY_TIMESTAMP,

    /**
     * Polymarket API key
     */
    POLY_API_KEY,

    /**
     * Polymarket API key passphrase
     */
    POLY_PASSPHRASE,
    ;
}