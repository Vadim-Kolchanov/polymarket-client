package dev.kolchanov.polymarket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.kolchanov.polymarket.dto.api.request.order.UserOrderRequest
import dev.kolchanov.polymarket.enums.TradeSide
import dev.kolchanov.polymarket.utils.MarketUtils
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertAll
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolymarketClientIntegrationTest {

    private val polymarketClient: PolymarketClient = PolymarketClient()
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AuthL2Api {
        @Test
        fun getApiKeysTest(): Unit = runBlocking {
            polymarketClient.privateApi.authL2Api.getApiKeys().await().apiKeys.forEach {
                assert(it.isNotBlank()) { "API key should not be blank" }
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class MarketsApi {
        @Test
        fun getMarketBySlugTest(): Unit = runBlocking {
            val slug = "bitcoin-up-or-down-november-17-12am-et"

            polymarketClient.publicApi.marketsApi.getMarketBySlug(slug).await().run {
                assertAll(
                    { assert(slug == slug) { "Market slug should match the requested slug" } },
                    { assert(id.isNotBlank()) { "Market ID should not be blank" } },
                )
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class OrderApi {
        private lateinit var orderId: String

        @Test
        @Order(1)
        fun createAndPlaceOrderTest(): Unit = runBlocking {
            val nowPlus2Hour = OffsetDateTime.now(ZoneOffset.UTC).plusHours(2)
            val market = polymarketClient.publicApi
                .marketsApi.getMarketBySlug(slug = MarketUtils.build15MinMarketSlug(now = nowPlus2Hour)).await()
            val assetId = objectMapper.readValue<List<String>>(market.clobTokenIds).first()

            polymarketClient.privateApi.orderApi.createAndPlaceOrder(
                userOrder = UserOrderRequest(
                    assetId = assetId,
                    side = TradeSide.BUY,
                    price = BigDecimal.valueOf(0.3),
                    size = BigDecimal.valueOf(5),
                ),
            ).await().run {
                assertAll(
                    { assertTrue(success) },
                    { assertNotNull(orderId) { "Order ID should not be null" } },
                )
                this@OrderApi.orderId = orderId!!
            }
        }

        @Test
        @Order(2)
        fun cancelOrder(): Unit = runBlocking {
            polymarketClient.privateApi.orderApi.cancelOrder(orderId = orderId).await().run {
                assertEquals(1, canceled.size) {
                    "There should be exactly one canceled order"
                }
                assertTrue(canceled.single() == orderId) {
                    "Canceled order ID should match the requested order ID"
                }
            }
        }
    }
}
