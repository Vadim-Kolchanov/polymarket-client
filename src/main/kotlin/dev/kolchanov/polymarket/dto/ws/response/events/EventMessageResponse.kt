package dev.kolchanov.polymarket.dto.ws.response.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "event_type",
    visible = true,
    defaultImpl = EventMessageResponse::class
)
@JsonSubTypes(
    JsonSubTypes.Type(value = PriceChangeEventMessageResponse::class, name = PriceChangeEventMessageResponse.EVENT_TYPE),
    JsonSubTypes.Type(value = TradeEventMessageResponse::class, name = TradeEventMessageResponse.EVENT_TYPE),
    JsonSubTypes.Type(value = OrderEventMessageResponse::class, name = OrderEventMessageResponse.EVENT_TYPE),
)
open class EventMessageResponse(
    @field:JsonProperty("event_type")
    val eventType: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventMessageResponse) return false
        if (eventType != other.eventType) return false
        return true
    }

    override fun hashCode(): Int = eventType.hashCode()
}