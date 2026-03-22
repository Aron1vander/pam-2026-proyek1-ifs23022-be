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

class MatchRepository(private val baseUrl: String) : IMatchRepository {

    override suspend fun getAll(userId: String, search: String, result: String, sport: String): List<Match> = suspendTransaction {
        val uid = UUID.fromString(userId)
        val conditions = mutableListOf(MatchTable.userId eq uid)
        if (result.isNotBlank()) conditions.add(MatchTable.result eq result.uppercase())
        if (sport.isNotBlank())  conditions.add(MatchTable.sport eq sport.lowercase())
        if (search.isNotBlank()) conditions.add(MatchTable.opponent.lowerCase() like "%${search.lowercase()}%")
        MatchDAO.find { conditions.reduce { acc, op -> acc and op } }
            .orderBy(MatchTable.matchDate to SortOrder.DESC)
            .map { matchDAOToModel(it, baseUrl) }
    }

    override suspend fun getById(matchId: String): Match? = suspendTransaction {
        MatchDAO.find { MatchTable.id eq UUID.fromString(matchId) }
            .limit(1).map { matchDAOToModel(it, baseUrl) }.firstOrNull()
    }

    override suspend fun create(match: Match): String = suspendTransaction {
        MatchDAO.new {
            userId = UUID.fromString(match.userId)
            opponent = match.opponent; venue = match.venue; sport = match.sport
            matchDate = match.matchDate; myScore = match.myScore
            opponentScore = match.opponentScore; result = match.result
            opponentLogo = match.opponentLogo; myLogo = match.myLogo
            // Tim sendiri
            goals = match.goals; assists = match.assists; shots = match.shots
            shotsOnTarget = match.shotsOnTarget; bigChances = match.bigChances
            bigChancesMissed = match.bigChancesMissed; tackles = match.tackles
            interceptions = match.interceptions; clearances = match.clearances
            saves = match.saves; blockedShots = match.blockedShots; fouls = match.fouls
            yellowCards = match.yellowCards; redCards = match.redCards
            offsides = match.offsides; possession = match.possession
            passAccuracy = match.passAccuracy; totalPasses = match.totalPasses
            corners = match.corners; dribbles = match.dribbles
            aerialDuelsWon = match.aerialDuelsWon; rebounds = match.rebounds
            steals = match.steals; blocks = match.blocks
            turnovers = match.turnovers; threePointers = match.threePointers
            // Lawan
            oppGoals = match.oppGoals; oppAssists = match.oppAssists
            oppShots = match.oppShots; oppShotsOnTarget = match.oppShotsOnTarget
            oppBigChances = match.oppBigChances; oppBigChancesMissed = match.oppBigChancesMissed
            oppTackles = match.oppTackles; oppInterceptions = match.oppInterceptions
            oppClearances = match.oppClearances; oppSaves = match.oppSaves
            oppBlockedShots = match.oppBlockedShots; oppFouls = match.oppFouls
            oppYellowCards = match.oppYellowCards; oppRedCards = match.oppRedCards
            oppOffsides = match.oppOffsides; oppPossession = match.oppPossession
            oppPassAccuracy = match.oppPassAccuracy; oppTotalPasses = match.oppTotalPasses
            oppCorners = match.oppCorners; oppDribbles = match.oppDribbles
            oppAerialDuelsWon = match.oppAerialDuelsWon; oppRebounds = match.oppRebounds
            oppSteals = match.oppSteals; oppBlocks = match.oppBlocks
            oppTurnovers = match.oppTurnovers; oppThreePointers = match.oppThreePointers
            notes = match.notes; createdAt = match.createdAt; updatedAt = match.updatedAt
        }.id.value.toString()
    }

    override suspend fun update(userId: String, matchId: String, newMatch: Match): Boolean = suspendTransaction {
        val dao = MatchDAO.find {
            (MatchTable.id eq UUID.fromString(matchId)) and
                    (MatchTable.userId eq UUID.fromString(userId))
        }.limit(1).firstOrNull() ?: return@suspendTransaction false
        dao.opponent = newMatch.opponent; dao.venue = newMatch.venue
        dao.sport = newMatch.sport; dao.matchDate = newMatch.matchDate
        dao.myScore = newMatch.myScore; dao.opponentScore = newMatch.opponentScore
        dao.result = newMatch.result
        // Tim sendiri
        dao.goals = newMatch.goals; dao.assists = newMatch.assists
        dao.shots = newMatch.shots; dao.shotsOnTarget = newMatch.shotsOnTarget
        dao.bigChances = newMatch.bigChances; dao.bigChancesMissed = newMatch.bigChancesMissed
        dao.tackles = newMatch.tackles; dao.interceptions = newMatch.interceptions
        dao.clearances = newMatch.clearances; dao.saves = newMatch.saves
        dao.blockedShots = newMatch.blockedShots; dao.fouls = newMatch.fouls
        dao.yellowCards = newMatch.yellowCards; dao.redCards = newMatch.redCards
        dao.offsides = newMatch.offsides; dao.possession = newMatch.possession
        dao.passAccuracy = newMatch.passAccuracy; dao.totalPasses = newMatch.totalPasses
        dao.corners = newMatch.corners; dao.dribbles = newMatch.dribbles
        dao.aerialDuelsWon = newMatch.aerialDuelsWon; dao.rebounds = newMatch.rebounds
        dao.steals = newMatch.steals; dao.blocks = newMatch.blocks
        dao.turnovers = newMatch.turnovers; dao.threePointers = newMatch.threePointers
        // Lawan
        dao.oppGoals = newMatch.oppGoals; dao.oppAssists = newMatch.oppAssists
        dao.oppShots = newMatch.oppShots; dao.oppShotsOnTarget = newMatch.oppShotsOnTarget
        dao.oppBigChances = newMatch.oppBigChances; dao.oppBigChancesMissed = newMatch.oppBigChancesMissed
        dao.oppTackles = newMatch.oppTackles; dao.oppInterceptions = newMatch.oppInterceptions
        dao.oppClearances = newMatch.oppClearances; dao.oppSaves = newMatch.oppSaves
        dao.oppBlockedShots = newMatch.oppBlockedShots; dao.oppFouls = newMatch.oppFouls
        dao.oppYellowCards = newMatch.oppYellowCards; dao.oppRedCards = newMatch.oppRedCards
        dao.oppOffsides = newMatch.oppOffsides; dao.oppPossession = newMatch.oppPossession
        dao.oppPassAccuracy = newMatch.oppPassAccuracy; dao.oppTotalPasses = newMatch.oppTotalPasses
        dao.oppCorners = newMatch.oppCorners; dao.oppDribbles = newMatch.oppDribbles
        dao.oppAerialDuelsWon = newMatch.oppAerialDuelsWon; dao.oppRebounds = newMatch.oppRebounds
        dao.oppSteals = newMatch.oppSteals; dao.oppBlocks = newMatch.oppBlocks
        dao.oppTurnovers = newMatch.oppTurnovers; dao.oppThreePointers = newMatch.oppThreePointers
        dao.notes = newMatch.notes; dao.updatedAt = newMatch.updatedAt
        true
    }

    override suspend fun updateLogo(userId: String, matchId: String, logoPath: String): Boolean = suspendTransaction {
        MatchDAO.find {
            (MatchTable.id eq UUID.fromString(matchId)) and (MatchTable.userId eq UUID.fromString(userId))
        }.limit(1).firstOrNull()?.also { it.opponentLogo = logoPath } != null
    }

    override suspend fun updateMyLogo(userId: String, matchId: String, logoPath: String): Boolean = suspendTransaction {
        MatchDAO.find {
            (MatchTable.id eq UUID.fromString(matchId)) and (MatchTable.userId eq UUID.fromString(userId))
        }.limit(1).firstOrNull()?.also { it.myLogo = logoPath } != null
    }

    override suspend fun delete(userId: String, matchId: String): Boolean = suspendTransaction {
        MatchTable.deleteWhere {
            (MatchTable.id eq UUID.fromString(matchId)) and (MatchTable.userId eq UUID.fromString(userId))
        } >= 1
    }
}