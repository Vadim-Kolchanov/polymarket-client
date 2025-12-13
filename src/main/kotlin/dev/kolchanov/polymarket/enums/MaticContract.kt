package dev.kolchanov.polymarket.enums

enum class MaticContract(val address: String) {
    EXCHANGE("0x4bFb41d5B3570DeFd03C39a9A4D8dE6Bd8B8982E"),
    NEG_RISK_ADAPTER("0xd91E80cF2E7be2e162c6513ceD06f1dD0dA35296"),
    NEG_RISK_EXCHANGE("0xC5d563A36AE78145C45a50134d48A1215220f80a"),

    /**
     * usdcAddress
     */
    COLLATERAL("0x2791Bca1f2de4661ED88A30C99A7a9449Aa84174"),

    /**
     * ctfAddress
     */
    CONDITIONAL_TOKENS("0x4D97DCd97eC945f40cF65F87097ACe5EA0476045"),
}