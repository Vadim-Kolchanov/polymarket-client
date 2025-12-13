package dev.kolchanov.polymarket.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PolymarketCredentials(
    /**
     * UUID identifying the credentials
     */
    val apiKey: String,
    /**
     * Secret string used to generate HMACs (not sent with requests)
     */
    val secret: String,
    /**
     * Secret string sent with each request, used to encrypt/decrypt the secret (never stored)
     */
    val passphrase: String,
)