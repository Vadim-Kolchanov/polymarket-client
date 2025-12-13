package dev.kolchanov.polymarket.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ClosedOnlyModeStatusResponse(
    @field:JsonProperty("closed_only")
    val closedOnly: Boolean,
)
