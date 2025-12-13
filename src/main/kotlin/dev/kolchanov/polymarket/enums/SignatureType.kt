package dev.kolchanov.polymarket.enums

enum class SignatureType(val index: Int) {
    /**
     * ECDSA EIP712 signatures signed by EOAs
     */
    EOA(index = 0),

    /**
     * EIP712 signatures signed by EOAs that own Polymarket Proxy wallets
     */
    POLY_PROXY(index = 1),

    /**
     * Наш вариант
     * EIP712 signatures signed by EOAs that own Polymarket Gnosis safes
     */
    POLY_GNOSIS_SAFE(index = 2),
}