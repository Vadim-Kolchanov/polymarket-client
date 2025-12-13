package dev.kolchanov.polymarket.dto.api.response.order

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CancelOrderResponse(
    /**
     * list of canceled orders
     */
    val canceled: List<String>,

    /**
     * order id -> reason map that explains why that order couldn’t be canceled
     */
    @field:JsonProperty("not_canceled")
    val notCanceled: Any?,
)
