package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object MatchGoalTable : UUIDTable("match_goals") {
    val matchId    = uuid("match_id").references(MatchTable.id)
    val playerName = varchar("player_name", 100)
    val minute     = integer("minute")
    val isOwnGoal  = bool("is_own_goal").default(false)
    val isPenalty  = bool("is_penalty").default(false)
    val team       = varchar("team", 10).default("my")  // "my" | "opponent"
    val createdAt  = timestamp("created_at")
}