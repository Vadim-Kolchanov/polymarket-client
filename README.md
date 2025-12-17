# ⚠️ Disclaimer

**This is NOT an official Polymarket library (yet).** This project is an independent and is not
affiliated with, endorsed by, or maintained by Polymarket.

I hope that Polymarket developers will review this code and approve it for safe use by the community.
Until official approval, use at your own risk. The author is not responsible for any financial losses or issues arising from the use of this
library.

# Polymarket Kotlin Client

[![Maven Central](https://img.shields.io/maven-central/v/dev.kolchanov/polymarket-client.svg)](https://central.sonatype.com/artifact/dev.kolchanov/polymarket-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org)

A Kotlin client library for interacting with the [Polymarket](https://polymarket.com) prediction market platform via
their CLOB (Central Limit Order Book) API.

---

## 📑 Contents

- [🧪 MVP Status](#-mvp-status)
- [🚀 Why Kotlin/Java?](#-why-kotlinjava)
- [📦 Installation](#-installation)
- [⚙️ Configuration](#️-configuration)
- [🔧 Usage](#-usage)
  - [Initialize the Client](#initialize-the-client)
  - [Public API](#public-api-no-authentication-required)
  - [Private API](#private-api-authentication-required)
  - [WebSocket Subscriptions](#websocket-subscriptions)
- [🔐 Authentication](#-authentication)
- [🛠️ Requirements](#️-requirements)
- [📄 License](#-license)
- [🤝 Contributing](#-contributing)
- [📬 Contact](#-contact)

---

## 🧪 MVP Status

This library is currently a **Minimum Viable Product (MVP)** covering core Polymarket API functionality.

If the community finds this library useful, I will continue to expand it in my free time by adding more Polymarket API endpoints and features.

**Currently supported:**
- ✅ L1/L2 Authentication
- ✅ Market data retrieval
- ✅ Order placement and cancellation
- ✅ WebSocket subscriptions

Feel free to open an issue or PR if you need specific functionality!

---

## 🚀 Why Kotlin/Java?

Polymarket provides official client libraries for [Python](https://github.com/Polymarket/py-clob-client)
and [TypeScript](https://github.com/Polymarket/clob-client). However, these languages have inherent limitations for
high-performance trading applications:

| Feature         | Python/TypeScript                              | Kotlin/JVM                            |
|-----------------|------------------------------------------------|---------------------------------------|
| **Parallelism** | Limited (GIL in Python, single-threaded in JS) | True multi-threading with coroutines  |
| **Throughput**  | Lower due to interpreter overhead              | High throughput with JIT compilation  |
| **Latency**     | Higher garbage collection pauses               | Optimized GC with low-latency options |
| **Type Safety** | Runtime errors possible                        | Compile-time type checking            |
| **Ecosystem**   | Limited server-side tooling                    | Rich enterprise ecosystem             |

This Kotlin client leverages:

- **Kotlin Coroutines** for efficient asynchronous operations
- **OkHttp** for high-performance HTTP requests
- **Web3j** for blockchain interactions and EIP-712 signing
- **Jackson** for fast JSON serialization

---

## 📦 Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.kolchanov:polymarket-client:0.0.2")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'dev.kolchanov:polymarket-client:0.0.2'
}
```

### Maven

```xml

<dependency>
    <groupId>dev.kolchanov</groupId>
    <artifactId>polymarket-client</artifactId>
    <version>0.0.2</version>
</dependency>
```

---

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in your project root:

```env
POLYGON_ADDRESS_WALLET=0xYourWalletAddress
PRIVATE_KEY_WALLET=your-private-key-without-0x-prefix
FUNDER_ADDRESS=0xYourFunderAddress
CHAIN_ID=137
```

| Variable                   | Description                          | Default                            |
|----------------------------|--------------------------------------|------------------------------------|
| `POLYGON_ADDRESS_WALLET`   | Your Polygon wallet address          | Required                           |
| `PRIVATE_KEY_WALLET`       | Private key for signing transactions | Required                           |
| `FUNDER_ADDRESS`           | Funder/proxy wallet address          | Required                           |
| `CHAIN_ID`                 | Polygon chain ID                     | `137` (Mainnet)                    |
| `POLYMARKET_CLOB_URL`      | CLOB API base URL                    | `https://clob.polymarket.com`      |
| `GAMMA_API_POLYMARKET_URL` | Gamma API base URL                   | `https://gamma-api.polymarket.com` |

---

## 🔧 Usage

### Initialize the Client

```kotlin
import dev.kolchanov.polymarket.PolymarketClient
import dev.kolchanov.polymarket.dto.PolymarketContext

// Using environment variables (recommended)
val client = PolymarketClient()

// Or with explicit configuration
val client = PolymarketClient(
    PolymarketContext(
        address = "0xYourWalletAddress",
        privateKey = "your-private-key",
        funderAddress = "0xYourFunderAddress",
        chainId = 137L,
        // A custom proxy wallet (SignatureType.POLY_PROXY) only used with users who logged in via Magic Link email/Google
        // else use Gnosis Safe multisig proxy wallet (most common)
        signatureType = SignatureType.POLY_GNOSIS_SAFE,
    )
)
```

### Public API (No Authentication Required)

#### Get Market Data

```kotlin
import kotlinx.coroutines.runBlocking

runBlocking {
    val market = client.publicApi.marketsApi
        .getMarketBySlug("what-price-will-bitcoin-hit-in-2025")
        .await()

    println("Market: ${market.question}")
    println("End Date: ${market.endDate}")
}
```

### Private API (Authentication Required)

#### Get API Keys

```kotlin
runBlocking {
    val apiKeys = client.privateApi.authL2Api.getApiKeys().await()
    println("API Keys: ${apiKeys.apiKeys}")
}
```

#### Place an Order

```kotlin
import dev.kolchanov.polymarket.dto.api.request.order.UserOrderRequest
import dev.kolchanov.polymarket.enums.TradeSide
import java.math.BigDecimal

runBlocking {
    val order = UserOrderRequest(
        assetId = "token-id-from-market",
        side = TradeSide.BUY,
        price = BigDecimal("0.50"),  // 50 cents
        size = BigDecimal("10")      // 10 shares
    )

    val result = client.privateApi.orderApi.createAndPlaceOrder(order).await()
    println("Order ID: ${result.orderID}")
}
```

#### Cancel an Order

```kotlin
runBlocking {
    val result = client.privateApi.orderApi.cancelOrder("order-id").await()
    println("Cancelled: ${result.canceled}")
}
```

### WebSocket Subscriptions

```kotlin
import dev.kolchanov.polymarket.websocket.impl.MarketWebSocket

val webSocket = MarketWebSocket(
    assetIds = listOf("token-id-1", "token-id-2"),
    onMessageHandle = { message ->
        println("Received: $message")
    }
)

webSocket.connect()

// Later, to disconnect
webSocket.shutdown()
```

---

## 🔐 Authentication

The library handles two levels of authentication:

### L1 Authentication (Wallet Signature)

Used for API key management. Signs requests using EIP-712 typed data with your wallet's private key.

### L2 Authentication (API Key)

Used for trading operations. Signs requests using HMAC with your API secret.

Credentials are automatically managed by `PolymarketClient` — on first access to `privateApi`, the client will derive
existing credentials or create new ones.

---

## 🛠️ Requirements

- **Java 21** or higher
- **Kotlin 2.0** or higher
- Polygon wallet with funds for trading

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📬 Contact

- **Author**: Vadim Kolchanov
- **Email**: kolchanov.offer@gmail.com
- [![LinkedIn](https://img.shields.io/badge/LinkedIn-%230077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/vadim-kolchanov/)
- [![Youtube](https://img.shields.io/badge/YouTube-%23E4405F.svg?logo=YouTube&logoColor=white)](https://www.youtube.com/@kolchanov-dev)
- [![Facebook](https://img.shields.io/badge/Facebook-%231877F2.svg?logo=Facebook&logoColor=white)](https://www.facebook.com/profile.php?id=100005202818751)
- [![Instagram](https://img.shields.io/badge/Instagram-%23E4405F.svg?logo=Instagram&logoColor=white)](https://www.instagram.com/kolchanov.dev/)

---

Happy trading on Polymarket! 🚀