package com.appohuatl.backend.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.appohuatl.backend.db.*
import com.appohuatl.backend.tokenomics.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.security.MessageDigest
import java.math.BigDecimal
import org.jetbrains.exposed.dao.id.EntityID

private val tokenomics = Tokenomics(TokenomicsConfig(), genesis = Instant.ofEpochSecond(1735689600)) // 2025-01-01 UTC

fun Application.registerRoutes() {
    routing {
        route("/auth") {
            post("/register") {
                val body = call.receive<RegisterRequest>()
                val id = transaction {
                    Users.insertAndGetId {
                        it[email] = body.email
                        it[passwordHash] = sha256(body.password)
                        it[fullName] = body.fullName
                    }.value
                }
                call.respond(HttpStatusCode.OK, mapOf("userId" to id.toString(), "email" to body.email))
            }
            post("/login") {
                val body = call.receive<LoginRequest>()
                val user = transaction {
                    Users.select { Users.email eq body.email }.firstOrNull()
                }
                val ok = user != null && user[Users.passwordHash] == sha256(body.password)
                if (!ok) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenciales inválidas"))
                } else {
                    val token = "token-" + UUID.randomUUID().toString()
                    call.respond(HttpStatusCode.OK, mapOf("accessToken" to token))
                }
            }
        }
        get("/users/me") {
            call.respond(HttpStatusCode.OK, mapOf("userId" to "u_123", "email" to "test@example.com", "fullName" to "Usuario Demo"))
        }
        post("/plantations") {
            val plantationId = transaction {
                // Ensure a demo user exists and use its id to satisfy FK
                val demoEmail = "demo@ohua.local"
                val uid = Users.select { Users.email eq demoEmail }
                    .firstOrNull()?.get(Users.id)?.value
                    ?: Users.insertAndGetId {
                        it[email] = demoEmail
                        it[passwordHash] = sha256("demo")
                        it[fullName] = "Demo User"
                    }.value

                Plantations.insertAndGetId {
                    it[userId] = EntityID(uid, Users)
                    it[name] = "Plantación Demo"
                    it[location] = "MX"
                    it[areaHectares] = null
                }.value
            }
            call.respond(HttpStatusCode.OK, mapOf("plantationId" to plantationId.toString()))
        }
        post("/readings") {
            val body = call.receive<ReadingRequest>()
            val id = transaction {
                Readings.insertAndGetId {
                    it[plantationId] = EntityID(UUID.fromString(body.plantationId), Plantations)
                    it[co2kg] = BigDecimal.valueOf(body.co2Kg)
                    it[readingAt] = Instant.parse(body.readingAt).epochSecond
                    it[createdAt] = Instant.now().epochSecond
                }.value
            }
            call.respond(HttpStatusCode.OK, mapOf("readingId" to id.toString(), "co2Kg" to body.co2Kg))
        }
        get("/balances/carbon") {
            val totalKg = transaction {
                Readings.slice(Readings.co2kg.sum()).selectAll().firstOrNull()?.getOrNull(Readings.co2kg.sum())?.toDouble() ?: 0.0
            }
            val minted = transaction {
                TokenMints.slice(TokenMints.amountTokens.sum()).selectAll().firstOrNull()?.getOrNull(TokenMints.amountTokens.sum())?.toDouble() ?: 0.0
            }
            val mintable = tokenomics.ohuaForCo2Kg(totalKg, minted)
            call.respond(HttpStatusCode.OK, mapOf("totalCo2Kg" to totalKg, "mintableTokens" to mintable, "minted" to minted))
        }
        post("/tokens/mint") {
            val body = call.receive<MintRequest>()
            val txHash = "tx_" + UUID.randomUUID().toString()
            transaction {
                val demoEmail = "demo@ohua.local"
                val uid = Users.select { Users.email eq demoEmail }
                    .firstOrNull()?.get(Users.id)?.value
                    ?: Users.insertAndGetId {
                        it[email] = demoEmail
                        it[passwordHash] = sha256("demo")
                        it[fullName] = "Demo User"
                    }.value
                TokenMints.insert {
                    it[userId] = EntityID(uid, Users)
                    it[amountTokens] = BigDecimal.valueOf(body.amountTokens)
                    it[TokenMints.txHash] = txHash
                    it[createdAt] = Instant.now().epochSecond
                }
            }
            // TODO: si existen variables SOROBAN_* se puede llamar al contrato aquí
            call.respond(HttpStatusCode.OK, mapOf("txHash" to txHash, "amountTokens" to body.amountTokens))
        }
        get("/tokens/transactions") {
            val items = transaction {
                TokenMints.selectAll().orderBy(TokenMints.createdAt to SortOrder.DESC).map {
                    mapOf(
                        "id" to it[TokenMints.id].value.toString(),
                        "amountTokens" to it[TokenMints.amountTokens].toDouble(),
                        "txHash" to it[TokenMints.txHash],
                        "createdAt" to it[TokenMints.createdAt]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, items)
        }

        // Simple history: last readings with derived tokens using current multiplier (visual)
        get("/history") {
            val rows = transaction {
                Readings.selectAll().orderBy(Readings.createdAt to SortOrder.DESC).limit(20).map {
                    val epoch = it[Readings.createdAt]
                    val day = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate()
                    val co2 = it[Readings.co2kg].toDouble()
                    val tokens = tokenomics.currentMultiplier() * co2
                    mapOf(
                        "day" to day.toString(),
                        "co2Kg" to co2,
                        "tokens" to tokens
                    )
                }
            }
            call.respond(HttpStatusCode.OK, rows)
        }
    }
}

@Serializable
data class ReadingRequest(val plantationId: String, val co2Kg: Double, val readingAt: String)

@Serializable
data class MintRequest(val amountTokens: Double)

@Serializable
data class RegisterRequest(val email: String, val password: String, val fullName: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

private fun sha256(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
