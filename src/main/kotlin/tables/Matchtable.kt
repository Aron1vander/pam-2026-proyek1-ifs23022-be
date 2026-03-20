package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MatchTable : UUIDTable("matches") {

    // Info Umum
    val userId        = uuid("user_id")
    val opponent      = varchar("opponent", 100)
    val venue         = varchar("venue", 100)
    val sport         = varchar("sport", 20)
    val matchDate     = date("match_date")
    val myScore       = integer("my_score")
    val opponentScore = integer("opponent_score")
    val result        = varchar("result", 1)        // "W" / "L" / "D"

    // Statistik Serangan
    val goals            = integer("goals")
    val assists          = integer("assists")
    val shots            = integer("shots")
    val shotsOnTarget    = integer("shots_on_target")
    val bigChances       = integer("big_chances")
    val bigChancesMissed = integer("big_chances_missed")

    // Statistik Bertahan
    val tackles       = integer("tackles")
    val interceptions = integer("interceptions")
    val clearances    = integer("clearances")
    val saves         = integer("saves")
    val blockedShots  = integer("blocked_shots")

    // Disiplin
    val fouls       = integer("fouls")
    val yellowCards = integer("yellow_cards")
    val redCards    = integer("red_cards")
    val offsides    = integer("offsides")

    // Penguasaan & Passing
    val possession     = integer("possession")
    val passAccuracy   = integer("pass_accuracy")
    val totalPasses    = integer("total_passes")
    val corners        = integer("corners")
    val dribbles       = integer("dribbles")
    val aerialDuelsWon = integer("aerial_duels_won")

    // Khusus Basketball
    val rebounds      = integer("rebounds")
    val steals        = integer("steals")
    val blocks        = integer("blocks")
    val turnovers     = integer("turnovers")
    val threePointers = integer("three_pointers")

    // Catatan
    val notes     = text("notes")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}