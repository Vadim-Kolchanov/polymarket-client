package dev.kolchanov.polymarket.api

import dev.kolchanov.polymarket.api.clob.AuthL1Api
import dev.kolchanov.polymarket.api.clob.AuthL2Api
import dev.kolchanov.polymarket.api.interceptor.AuthL1Interceptor
import dev.kolchanov.polymarket.api.interceptor.AuthL2Interceptor
import dev.kolchanov.polymarket.dto.api.response.PolymarketCredentials
import dev.kolchanov.polymarket.enums.system.SystemEnv
import dev.kolchanov.polymarket.exception.ApiException
import dev.kolchanov.polymarket.utils.Eip712Model
import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthApiIntegrationTest {

    private val authL1Api: AuthL1Api = AuthL1Api(
        authL1Interceptor = AuthL1Interceptor(
            chainId = 137L,
            address = SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
            privateKey = SystemEnv.PRIVATE_KEY_WALLET.getEnvOrDefault(),
            nonce = Eip712Model.NONCE,
        ),
    )

    private lateinit var credentials: PolymarketCredentials
    private val authL2Api: AuthL2Api by lazy {
        AuthL2Api(
            authL2Interceptor = AuthL2Interceptor(
                address = SystemEnv.POLYGON_ADDRESS_WALLET.getEnvOrDefault(),
                credentials = credentials,
            ),
        )
    }

    @Test
    @Order(1)
    fun createApiKeyTest() = runBlocking {
        try {
            authL1Api.createApiKey().await().run {
                assertAll(
                    { assertTrue(apiKey.isNotBlank()) },
                    { assertTrue(secret.isNotBlank()) },
                    { assertTrue(passphrase.isNotBlank()) },
                )
            }
        } catch (e: ApiException) {
            assertAll(
                { assertTrue(e.status == HttpResponseStatus.BAD_REQUEST) },
                { assertTrue(e.message?.contains("Could not create api key") == true) },
            )
        }
    }

    @Test
    @Order(2)
    fun deriveApiKeyTest() = runBlocking {
        credentials = authL1Api.deriveApiKey().await().apply {
            assertAll(
                { assertTrue(apiKey.isNotBlank()) },
                { assertTrue(secret.isNotBlank()) },
                { assertTrue(passphrase.isNotBlank()) },
            )
        }
    }

    @Test
    @Order(3)
    fun getApiKeysTest() = runBlocking {
        authL2Api.getApiKeys().await().run {
            apiKeys.forEach {
                assertTrue(it.isNotBlank()) { "API key should not be blank. ApiKeys: $apiKeys" }
            }
        }
    }

    @Test
    @Order(4)
    fun getClosedOnlyModeStatusTest() = runBlocking {
        authL2Api.getClosedOnlyModeStatus().await().run {
            assertTrue(closedOnly.not()) { "Closed-only mode should be false by default." }
        }
    }

    @Test
    @Order(5)
    fun deleteApiKeyTest() = runBlocking {
        authL2Api.deleteApiKey().await().run {
            assertAll(
                { assertNotNull(this) },
                { assertEquals("OK", this) },
            )
        }
    }
}