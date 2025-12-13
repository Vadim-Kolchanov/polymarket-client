package dev.kolchanov.polymarket.exception

import io.netty.handler.codec.http.HttpResponseStatus

class ApiException(
    message: String,
    val status: HttpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR,
) : RuntimeException(message)
