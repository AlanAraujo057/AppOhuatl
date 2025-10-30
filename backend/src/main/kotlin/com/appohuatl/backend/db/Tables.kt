package com.appohuatl.backend.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table

object Users : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val fullName = varchar("full_name", 255)
}

object Wallets : UUIDTable("wallets") {
    val userId = reference("user_id", Users)
    val stellarAddress = varchar("stellar_address", 120).uniqueIndex()
    val isVerified = bool("is_verified").default(false)
}

object Plantations : UUIDTable("plantations") {
    val userId = reference("user_id", Users)
    val name = varchar("name", 255)
    val location = varchar("location", 255).nullable()
    val areaHectares = decimal("area_hectares", 12, 2).nullable()
}

object Readings : UUIDTable("readings") {
    val plantationId = reference("plantation_id", Plantations)
    val co2kg = decimal("co2_kg", 18, 6)
    val readingAt = long("reading_at")
    val createdAt = long("created_at")
}

object TokenMints : UUIDTable("token_mints") {
    val userId = reference("user_id", Users)
    val amountTokens = decimal("amount_tokens", 18, 6)
    val txHash = varchar("tx_hash", 255).uniqueIndex()
    val createdAt = long("created_at")
}
