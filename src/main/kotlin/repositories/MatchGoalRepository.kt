package org.delcom.repositories

import kotlinx.datetime.Clock
import org.delcom.dao.MatchGoalDAO
import org.delcom.entities.MatchGoal
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.MatchGoalTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.util.UUID

class MatchGoalRepository : IMatchGoalRepository {

    override suspend fun getByMatchId(matchId: String): List<MatchGoal> = suspendTransaction {
        MatchGoalDAO.find { MatchGoalTable.matchId eq UUID.fromString(matchId) }
            .orderBy(MatchGoalTable.minute to SortOrder.ASC)
            .map { dao ->
                MatchGoal(
                    id         = dao.id.value.toString(),
                    matchId    = dao.matchId.toString(),
                    playerName = dao.playerName,
                    minute     = dao.minute,
                    isOwnGoal  = dao.isOwnGoal,
                    isPenalty  = dao.isPenalty,
                    team       = dao.team,
                    createdAt  = dao.createdAt,
                )
            }
    }

    override suspend fun create(goal: MatchGoal): String = suspendTransaction {
        MatchGoalDAO.new {
            matchId    = UUID.fromString(goal.matchId)
            playerName = goal.playerName
            minute     = goal.minute
            isOwnGoal  = goal.isOwnGoal
            isPenalty  = goal.isPenalty
            team       = goal.team
            createdAt  = Clock.System.now()
        }.id.value.toString()
    }

    override suspend fun delete(goalId: String, matchId: String): Boolean = suspendTransaction {
        MatchGoalTable.deleteWhere {
            (MatchGoalTable.id eq UUID.fromString(goalId)) and
                    (MatchGoalTable.matchId eq UUID.fromString(matchId))
        } >= 1
    }
}