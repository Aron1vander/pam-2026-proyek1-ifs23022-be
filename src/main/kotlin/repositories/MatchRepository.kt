package org.delcom.repositories

import org.delcom.dao.MatchDAO
import org.delcom.entities.Match
import org.delcom.helpers.matchDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.MatchTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class MatchRepository : IMatchRepository {

    override suspend fun getAll(
        userId: String,
        search: String,
        result: String,
        sport: String,
    ): List<Match> = suspendTransaction {
        val uid = UUID.fromString(userId)

        val conditions = mutableListOf(MatchTable.userId eq uid)

        if (result.isNotBlank()) {
            conditions.add(MatchTable.result eq result.uppercase())
        }

        if (sport.isNotBlank()) {
            conditions.add(MatchTable.sport eq sport.lowercase())
        }

        if (search.isNotBlank()) {
            val keyword = "%${search.lowercase()}%"
            conditions.add(MatchTable.opponent.lowerCase() like keyword)
        }

        MatchDAO
            .find { conditions.reduce { acc, op -> acc and op } }
            .orderBy(MatchTable.matchDate to SortOrder.DESC)
            .map(::matchDAOToModel)
    }

    override suspend fun getById(matchId: String): Match? = suspendTransaction {
        MatchDAO
            .find { MatchTable.id eq UUID.fromString(matchId) }
            .limit(1)
            .map(::matchDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(match: Match): String = suspendTransaction {
        val dao = MatchDAO.new {
            userId           = UUID.fromString(match.userId)
            opponent         = match.opponent
            venue            = match.venue
            sport            = match.sport
            matchDate        = match.matchDate
            myScore          = match.myScore
            opponentScore    = match.opponentScore
            result           = match.result
            goals            = match.goals
            assists          = match.assists
            shots            = match.shots
            shotsOnTarget    = match.shotsOnTarget
            bigChances       = match.bigChances
            bigChancesMissed = match.bigChancesMissed
            tackles          = match.tackles
            interceptions    = match.interceptions
            clearances       = match.clearances
            saves            = match.saves
            blockedShots     = match.blockedShots
            fouls            = match.fouls
            yellowCards      = match.yellowCards
            redCards         = match.redCards
            offsides         = match.offsides
            possession       = match.possession
            passAccuracy     = match.passAccuracy
            totalPasses      = match.totalPasses
            corners          = match.corners
            dribbles         = match.dribbles
            aerialDuelsWon   = match.aerialDuelsWon
            rebounds         = match.rebounds
            steals           = match.steals
            blocks           = match.blocks
            turnovers        = match.turnovers
            threePointers    = match.threePointers
            notes            = match.notes
            createdAt        = match.createdAt
            updatedAt        = match.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun update(userId: String, matchId: String, newMatch: Match): Boolean = suspendTransaction {
        val dao = MatchDAO
            .find {
                (MatchTable.id eq UUID.fromString(matchId)) and
                        (MatchTable.userId eq UUID.fromString(userId))
            }
            .limit(1)
            .firstOrNull() ?: return@suspendTransaction false

        dao.opponent         = newMatch.opponent
        dao.venue            = newMatch.venue
        dao.sport            = newMatch.sport
        dao.matchDate        = newMatch.matchDate
        dao.myScore          = newMatch.myScore
        dao.opponentScore    = newMatch.opponentScore
        dao.result           = newMatch.result
        dao.goals            = newMatch.goals
        dao.assists          = newMatch.assists
        dao.shots            = newMatch.shots
        dao.shotsOnTarget    = newMatch.shotsOnTarget
        dao.bigChances       = newMatch.bigChances
        dao.bigChancesMissed = newMatch.bigChancesMissed
        dao.tackles          = newMatch.tackles
        dao.interceptions    = newMatch.interceptions
        dao.clearances       = newMatch.clearances
        dao.saves            = newMatch.saves
        dao.blockedShots     = newMatch.blockedShots
        dao.fouls            = newMatch.fouls
        dao.yellowCards      = newMatch.yellowCards
        dao.redCards         = newMatch.redCards
        dao.offsides         = newMatch.offsides
        dao.possession       = newMatch.possession
        dao.passAccuracy     = newMatch.passAccuracy
        dao.totalPasses      = newMatch.totalPasses
        dao.corners          = newMatch.corners
        dao.dribbles         = newMatch.dribbles
        dao.aerialDuelsWon   = newMatch.aerialDuelsWon
        dao.rebounds         = newMatch.rebounds
        dao.steals           = newMatch.steals
        dao.blocks           = newMatch.blocks
        dao.turnovers        = newMatch.turnovers
        dao.threePointers    = newMatch.threePointers
        dao.notes            = newMatch.notes
        dao.updatedAt        = newMatch.updatedAt
        true
    }

    override suspend fun delete(userId: String, matchId: String): Boolean = suspendTransaction {
        val rows = MatchTable.deleteWhere {
            (MatchTable.id eq UUID.fromString(matchId)) and
                    (MatchTable.userId eq UUID.fromString(userId))
        }
        rows >= 1
    }
}