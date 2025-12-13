package dev.kolchanov.polymarket.api.interceptor

import dev.kolchanov.polymarket.enums.header.PolymarketL1Header
import dev.kolchanov.polymarket.utils.Eip712Signer
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor for L1 (wallet-based) authentication with Polymarket API.
 *
 * Adds required authentication headers to each request:
 * - [PolymarketL1Header.POLY_ADDRESS] - Wallet address
 * - [PolymarketL1Header.POLY_SIGNATURE] - EIP-712 signature
 * - [PolymarketL1Header.POLY_TIMESTAMP] - Current Unix timestamp
 * - [PolymarketL1Header.POLY_NONCE] - Nonce value
 *
 * @property chainId Blockchain chain ID for signature.
 * @property address Polygon wallet address.
 * @property privateKey Wallet private key for signing.
 * @property nonce Nonce value for EIP-712 signature.
 *
 * @see Eip712Signer.signClobPolymarketAuthTypedData
 */
class AuthL1Interceptor(
    private val chainId: Long,
    private val address: String,
    private val privateKey: String,
    private val nonce: Long,
) : Interceptor {

    init {
        require(address.isNotBlank()) { "Address must not be blank" }
        require(privateKey.isNotBlank()) { "PrivateKey must not be blank" }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val timestamp = System.currentTimeMillis() / 1000
        val valueByHeader: Map<PolymarketL1Header, String> = mapOf(
            PolymarketL1Header.POLY_ADDRESS to address,
            PolymarketL1Header.POLY_SIGNATURE to Eip712Signer.signClobPolymarketAuthTypedData(
                chainId = chainId,
                address = address,
                timestamp = timestamp,
                privateKey = privateKey,
            ),
            PolymarketL1Header.POLY_TIMESTAMP to timestamp.toString(),
            PolymarketL1Header.POLY_NONCE to nonce.toString(),
        )

        return chain.request().newBuilder()
            .apply { valueByHeader.forEach { addHeader(it.key.name, it.value) } }
            .build()
            .let(chain::proceed)
    }
}