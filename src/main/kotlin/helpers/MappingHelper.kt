package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.MatchDAO
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.UserDAO
import org.delcom.entities.Match
import org.delcom.entities.RefreshToken
import org.delcom.entities.User
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDAOToModel(dao: UserDAO, baseUrl: String) = User(
    id          = dao.id.value.toString(),
    name        = dao.name, username = dao.username, password = dao.password,
    photo       = dao.photo,
    urlPhoto    = buildImageUrl(baseUrl, dao.photo ?: "/uploads/defaults/user.png"),
    teamLogo    = dao.teamLogo,
    urlTeamLogo = if (dao.teamLogo != null) buildImageUrl(baseUrl, dao.teamLogo!!) else "",
    createdAt   = dao.createdAt, updatedAt = dao.updatedAt,
)

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    id = dao.id.value.toString(), userId = dao.userId.toString(),
    refreshToken = dao.refreshToken, authToken = dao.authToken, createdAt = dao.createdAt,
)

fun matchDAOToModel(dao: MatchDAO, baseUrl: String) = Match(
    id            = dao.id.value.toString(),
    userId        = dao.userId.toString(),
    opponent      = dao.opponent, venue = dao.venue, sport = dao.sport,
    matchDate     = dao.matchDate, myScore = dao.myScore,
    opponentScore = dao.opponentScore, result = dao.result,
    opponentLogo    = dao.opponentLogo,
    urlOpponentLogo = if (dao.opponentLogo != null) buildImageUrl(baseUrl, dao.opponentLogo!!) else "",
    myLogo          = dao.myLogo,
    urlMyLogo       = if (dao.myLogo != null) buildImageUrl(baseUrl, dao.myLogo!!) else "",

    // Tim Sendiri
    goals = dao.goals, assists = dao.assists, shots = dao.shots,
    shotsOnTarget = dao.shotsOnTarget, bigChances = dao.bigChances,
    bigChancesMissed = dao.bigChancesMissed, tackles = dao.tackles,
    interceptions = dao.interceptions, clearances = dao.clearances,
    saves = dao.saves, blockedShots = dao.blockedShots, fouls = dao.fouls,
    yellowCards = dao.yellowCards, redCards = dao.redCards, offsides = dao.offsides,
    possession = dao.possession, passAccuracy = dao.passAccuracy,
    totalPasses = dao.totalPasses, corners = dao.corners, dribbles = dao.dribbles,
    aerialDuelsWon = dao.aerialDuelsWon, rebounds = dao.rebounds, steals = dao.steals,
    blocks = dao.blocks, turnovers = dao.turnovers, threePointers = dao.threePointers,

    // Lawan
    oppGoals = dao.oppGoals, oppAssists = dao.oppAssists, oppShots = dao.oppShots,
    oppShotsOnTarget = dao.oppShotsOnTarget, oppBigChances = dao.oppBigChances,
    oppBigChancesMissed = dao.oppBigChancesMissed, oppTackles = dao.oppTackles,
    oppInterceptions = dao.oppInterceptions, oppClearances = dao.oppClearances,
    oppSaves = dao.oppSaves, oppBlockedShots = dao.oppBlockedShots, oppFouls = dao.oppFouls,
    oppYellowCards = dao.oppYellowCards, oppRedCards = dao.oppRedCards,
    oppOffsides = dao.oppOffsides, oppPossession = dao.oppPossession,
    oppPassAccuracy = dao.oppPassAccuracy, oppTotalPasses = dao.oppTotalPasses,
    oppCorners = dao.oppCorners, oppDribbles = dao.oppDribbles,
    oppAerialDuelsWon = dao.oppAerialDuelsWon, oppRebounds = dao.oppRebounds,
    oppSteals = dao.oppSteals, oppBlocks = dao.oppBlocks, oppTurnovers = dao.oppTurnovers,
    oppThreePointers = dao.oppThreePointers,

    notes = dao.notes, createdAt = dao.createdAt, updatedAt = dao.updatedAt,
)

fun buildImageUrl(baseUrl: String, pathGambar: String): String {
    val relativePath = pathGambar.removePrefix("uploads/")
    return "$baseUrl/static/$relativePath"
}