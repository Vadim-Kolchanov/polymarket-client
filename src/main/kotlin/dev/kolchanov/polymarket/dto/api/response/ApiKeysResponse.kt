package dev.kolchanov.polymarket.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiKeysResponse(
    val apiKeys: List<String>,
)
