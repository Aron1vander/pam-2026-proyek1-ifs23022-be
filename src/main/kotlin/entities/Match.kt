package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Match(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var opponent: String,
    var venue: String = "",
    var sport: String = "football",

    @Contextual
    var matchDate: LocalDate,

    var myScore: Int = 0,
    var opponentScore: Int = 0,
    var result: String = "",
    var opponentLogo: String? = null,
    var urlOpponentLogo: String = "",
    var myLogo: String? = null,           // ← override logo tim per match
    var urlMyLogo: String = "",           // ← URL publik logo tim per match

    var goals: Int = 0,
    var assists: Int = 0,
    var shots: Int = 0,
    var shotsOnTarget: Int = 0,
    var bigChances: Int = 0,
    var bigChancesMissed: Int = 0,
    var tackles: Int = 0,
    var interceptions: Int = 0,
    var clearances: Int = 0,
    var saves: Int = 0,
    var blockedShots: Int = 0,
    var fouls: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0,
    var offsides: Int = 0,
    var possession: Int = 0,
    var passAccuracy: Int = 0,
    var totalPasses: Int = 0,
    var corners: Int = 0,
    var dribbles: Int = 0,
    var aerialDuelsWon: Int = 0,
    var rebounds: Int = 0,
    var steals: Int = 0,
    var blocks: Int = 0,
    var turnovers: Int = 0,
    var threePointers: Int = 0,
    var notes: String = "",

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
) {
    fun calculateResult(): Match {
        result = when {
            myScore > opponentScore -> "W"
            myScore < opponentScore -> "L"
            else -> "D"
        }
        return this
    }
}