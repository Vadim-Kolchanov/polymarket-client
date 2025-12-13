package dev.kolchanov.polymarket.dto

import dev.kolchanov.polymarket.api.interceptor.AuthL1Interceptor
import dev.kolchanov.polymarket.enums.SignatureType
import dev.kolchanov.polymarket.enums.system.SystemEnv
import dev.kolchanov.polymarket.utils.Eip712Model

/**
 * Configuration context for Polymarket client.
 *
 * Contains wallet credentials and chain settings required for authentication and signing.
 * Default values are loaded from environment variables via [SystemEnv].
 *
 * @property chainId Blockchain chain ID. Default: from [SystemEnv.CHAIN_ID].
 * @property address Polygon wallet address. Default: from [SystemEnv.POLYGON_ADDRESS_WALLET].
 * @property privateKey Wallet private key for signing transactions. Default: from [SystemEnv.PRIVATE_KEY_WALLET].
 * @property nonce Nonce value for EIP-712 signatures. Default: [Eip712Model.NONCE].
 * @property funderAddress Address of the funder (proxy wallet). Default: from [SystemEnv.FUNDER_ADDRESS].
 * @property signatureType Type of signature to use. Default: [SignatureType.POLY_GNOSIS_SAFE].
 */
data class PolymarketContext(
    val chainId: Long = SystemEnv.CHAIN_ID.getEnvOrDefault().toLong(),
    val address: String = SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
    val privateKey: String = SystemEnv.PRIVATE_KEY_WALLET.getEnvOrDefault(),
    val nonce: Long = Eip712Model.NONCE,
    val funderAddress: String = SystemEnv.FUNDER_ADDRESS.getEnvOrDefault(),
    val signatureType: SignatureType = SignatureType.POLY_GNOSIS_SAFE,
) {

    /**
     * Creates an [AuthL1Interceptor] from this context.
     *
     * @return Configured L1 authentication interceptor for OkHttp.
     */
    fun toAuthL1Interceptor() = AuthL1Interceptor(
        chainId = chainId,
        address = address,
        privateKey = privateKey,
        nonce = nonce,
    )
}
