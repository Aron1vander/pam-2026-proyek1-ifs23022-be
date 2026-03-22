package org.delcom.dao

import org.delcom.tables.MatchGoalTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class MatchGoalDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, MatchGoalDAO>(MatchGoalTable)

    var matchId    by MatchGoalTable.matchId
    var playerName by MatchGoalTable.playerName
    var minute     by MatchGoalTable.minute
    var isOwnGoal  by MatchGoalTable.isOwnGoal
    var isPenalty  by MatchGoalTable.isPenalty
    var team       by MatchGoalTable.team
    var createdAt  by MatchGoalTable.createdAt
}