package com.appohuatl.backend.tokenomics

import java.time.Instant
import java.time.Duration
import kotlin.math.floor

data class TokenomicsConfig(
    val initialOhuaPerKg: Double = 1.0,          // 1 Ohua por kg al inicio
    val halvingIntervalDays: Long = 365,         // cada 12 meses aprox
    val maxSupply: Double = 100_000_000.0        // tope de oferta total
)

class Tokenomics(private val config: TokenomicsConfig, private val genesis: Instant) {
    fun currentMultiplier(now: Instant = Instant.now()): Double {
        val elapsedDays = Duration.between(genesis, now).toDays()
        val halvings = if (config.halvingIntervalDays <= 0) 0 else floor(elapsedDays.toDouble() / config.halvingIntervalDays).toInt()
        return config.initialOhuaPerKg * Math.pow(0.5, halvings.toDouble())
    }

    fun ohuaForCo2Kg(co2Kg: Double, mintedSoFar: Double, now: Instant = Instant.now()): Double {
        val m = currentMultiplier(now)
        val desired = co2Kg * m
        val remaining = (config.maxSupply - mintedSoFar).coerceAtLeast(0.0)
        return desired.coerceAtMost(remaining)
    }
}
