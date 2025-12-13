package dev.kolchanov.polymarket.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.kolchanov.polymarket.dto.api.request.order.CreateAndPlaceOrderRequest
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric

object Eip712Signer {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun signClobPolymarketAuthTypedData(
        chainId: Long,
        address: String,
        timestamp: Long,
        privateKey: String,
    ): String = this.signTypedData(
        typedData = Eip712Model.PolymarketClobAuth.of(
            chainId = chainId,
            address = address,
            timestamp = timestamp,
        ),
        privateKey = privateKey,
    )

    fun signClobPolymarketOrderTypedData(
        chainId: Long,
        privateKey: String,
        order: CreateAndPlaceOrderRequest.Order,
    ): String = order.run {
        Eip712Model.PolymarketClobOrder.of(
            chainId = chainId,
            order = Eip712Model.PolymarketClobOrder.OrderMessage(
                salt = salt.toString(),
                maker = maker,
                signer = signer,
                taker = taker,
                tokenId = tokenId,
                makerAmount = makerAmount,
                takerAmount = takerAmount,
                expiration = expiration,
                nonce = nonce,
                feeRateBps = feeRateBps,
                side = side.index.toString(),
                signatureType = signatureType.toString(),
            ),
        )
    }.let { this.signTypedData(typedData = it, privateKey = privateKey) }

    private fun <T> signTypedData(typedData: T, privateKey: String): String {
        val signature: Sign.SignatureData = Sign.signTypedData(
            /* jsonData = */ objectMapper.writeValueAsString(typedData),
            /* keyPair = */ Credentials.create(privateKey).ecKeyPair,
        )

        val retval = ByteArray(65)
        System.arraycopy(signature.r, 0, retval, 0, 32)
        System.arraycopy(signature.s, 0, retval, 32, 32)
        System.arraycopy(signature.v, 0, retval, 64, 1)

        return Numeric.toHexString(retval)
    }
}
