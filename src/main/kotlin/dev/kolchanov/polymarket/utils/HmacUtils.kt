package dev.kolchanov.polymarket.utils

import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtils {

    private const val HMAC_ALGORITHM = "HmacSHA256"

    fun getHmacPolymarketSignature(
        secret: String,
        timestamp: Long,
        method: String,
        requestPath: String,
        body: String? = null,
    ): String {
        val message = "$timestamp$method$requestPath${body ?: ""}"

        val base64Secret = Base64.getUrlDecoder().decode(secret)
        val secretKey = SecretKeySpec(base64Secret, HMAC_ALGORITHM)

        val signatureBytes = Mac.getInstance(HMAC_ALGORITHM).run {
            init(secretKey)
            doFinal(message.toByteArray())
        }

        return Base64.getUrlEncoder().encodeToString(signatureBytes)
    }
}