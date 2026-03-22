package org.delcom.dao

import org.delcom.tables.MatchTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class MatchDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, MatchDAO>(MatchTable)

    var userId        by MatchTable.userId
    var opponent      by MatchTable.opponent
    var venue         by MatchTable.venue
    var sport         by MatchTable.sport
    var matchDate     by MatchTable.matchDate
    var myScore       by MatchTable.myScore
    var opponentScore by MatchTable.opponentScore
    var result        by MatchTable.result
    var opponentLogo  by MatchTable.opponentLogo
    var myLogo        by MatchTable.myLogo

    // Tim Sendiri
    var goals            by MatchTable.goals
    var assists          by MatchTable.assists
    var shots            by MatchTable.shots
    var shotsOnTarget    by MatchTable.shotsOnTarget
    var bigChances       by MatchTable.bigChances
    var bigChancesMissed by MatchTable.bigChancesMissed
    var tackles          by MatchTable.tackles
    var interceptions    by MatchTable.interceptions
    var clearances       by MatchTable.clearances
    var saves            by MatchTable.saves
    var blockedShots     by MatchTable.blockedShots
    var fouls            by MatchTable.fouls
    var yellowCards      by MatchTable.yellowCards
    var redCards         by MatchTable.redCards
    var offsides         by MatchTable.offsides
    var possession       by MatchTable.possession
    var passAccuracy     by MatchTable.passAccuracy
    var totalPasses      by MatchTable.totalPasses
    var corners          by MatchTable.corners
    var dribbles         by MatchTable.dribbles
    var aerialDuelsWon   by MatchTable.aerialDuelsWon
    var rebounds         by MatchTable.rebounds
    var steals           by MatchTable.steals
    var blocks           by MatchTable.blocks
    var turnovers        by MatchTable.turnovers
    var threePointers    by MatchTable.threePointers

    // Lawan
    var oppGoals            by MatchTable.oppGoals
    var oppAssists          by MatchTable.oppAssists
    var oppShots            by MatchTable.oppShots
    var oppShotsOnTarget    by MatchTable.oppShotsOnTarget
    var oppBigChances       by MatchTable.oppBigChances
    var oppBigChancesMissed by MatchTable.oppBigChancesMissed
    var oppTackles          by MatchTable.oppTackles
    var oppInterceptions    by MatchTable.oppInterceptions
    var oppClearances       by MatchTable.oppClearances
    var oppSaves            by MatchTable.oppSaves
    var oppBlockedShots     by MatchTable.oppBlockedShots
    var oppFouls            by MatchTable.oppFouls
    var oppYellowCards      by MatchTable.oppYellowCards
    var oppRedCards         by MatchTable.oppRedCards
    var oppOffsides         by MatchTable.oppOffsides
    var oppPossession       by MatchTable.oppPossession
    var oppPassAccuracy     by MatchTable.oppPassAccuracy
    var oppTotalPasses      by MatchTable.oppTotalPasses
    var oppCorners          by MatchTable.oppCorners
    var oppDribbles         by MatchTable.oppDribbles
    var oppAerialDuelsWon   by MatchTable.oppAerialDuelsWon
    var oppRebounds         by MatchTable.oppRebounds
    var oppSteals           by MatchTable.oppSteals
    var oppBlocks           by MatchTable.oppBlocks
    var oppTurnovers        by MatchTable.oppTurnovers
    var oppThreePointers    by MatchTable.oppThreePointers

    var notes     by MatchTable.notes
    var createdAt by MatchTable.createdAt
    var updatedAt by MatchTable.updatedAt
}