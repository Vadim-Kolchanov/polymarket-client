package dev.kolchanov.polymarket.dto.api.response.order

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * ``` json
 * {
 *  "errorMsg":"",
 *  "orderID":"0x8fc75ea5bf5eca24958d47fe02a78479134bce9b64e61cff74e6abd3226805f4",
 *  "takingAmount":"",
 *  "makingAmount":"",
 *  "status":"live",
 *  "success":true
 * }
 * ```
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateAndPlaceOrderResponse(
    /**
     * boolean indicating if server-side err (success = false) -> server-side error
     */
    val success: Boolean,
    /**
     * error message in case of unsuccessful placement (in case success = false, e.g. client-side error, the reason is in errorMsg)
     */
    val errorMsg: String?,
    /**
     * id of order
     */
    @field:JsonProperty("orderID")
    val orderId: String?,
    /**
     * hash of settlement transaction order was marketable and triggered a match
     */
    val orderHashes: List<String>?
)