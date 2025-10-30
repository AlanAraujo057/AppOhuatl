package com.appohuatl.backend.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import java.time.Instant
import java.util.UUID

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:h2:file:./build/ohua-db;AUTO_SERVER=TRUE;MODE=PostgreSQL;DATABASE_TO_UPPER=false",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(Users, Wallets, Plantations, Readings, TokenMints)
            // Seed demo data if empty so the app shows values
            val hasReadings = Readings.selectAll().limit(1).empty().not()
            if (!hasReadings) {
                val demoUserId = Users.insertAndGetId {
                    it[email] = "seed@ohua.local"
                    it[passwordHash] = "seed"
                    it[fullName] = "Seed User"
                }
                val pid = Plantations.insert {
                    it[userId] = demoUserId
                    it[name] = "Plantación Demo"
                    it[location] = "MX"
                    it[areaHectares] = null
                } get Plantations.id

                Readings.insert {
                    it[plantationId] = pid
                    it[co2kg] = java.math.BigDecimal("100.0")
                    it[readingAt] = Instant.now().epochSecond
                    it[createdAt] = Instant.now().epochSecond
                }
            }
        }
    }
}
