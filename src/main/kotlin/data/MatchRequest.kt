package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.delcom.entities.Match

@Serializable
data class MatchRequest(
    var userId: String = "",

    // ── Info Umum ──────────────────────────────────────────────────────────
    var opponent: String = "",
    var venue: String = "",
    var sport: String = "football",

    @Contextual
    var matchDate: LocalDate = LocalDate(2000, 1, 1),

    var myScore: Int = 0,
    var opponentScore: Int = 0,

    // ── Statistik Serangan ─────────────────────────────────────────────────
    var goals: Int = 0,
    var assists: Int = 0,
    var shots: Int = 0,
    var shotsOnTarget: Int = 0,
    var bigChances: Int = 0,
    var bigChancesMissed: Int = 0,

    // ── Statistik Bertahan ─────────────────────────────────────────────────
    var tackles: Int = 0,
    var interceptions: Int = 0,
    var clearances: Int = 0,
    var saves: Int = 0,
    var blockedShots: Int = 0,

    // ── Disiplin ───────────────────────────────────────────────────────────
    var fouls: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0,
    var offsides: Int = 0,

    // ── Penguasaan & Passing ───────────────────────────────────────────────
    var possession: Int = 0,
    var passAccuracy: Int = 0,
    var totalPasses: Int = 0,
    var corners: Int = 0,
    var dribbles: Int = 0,
    var aerialDuelsWon: Int = 0,

    // ── Khusus Basketball ─────────────────────────────────────────────────
    var rebounds: Int = 0,
    var steals: Int = 0,
    var blocks: Int = 0,
    var turnovers: Int = 0,
    var threePointers: Int = 0,

    // ── Catatan ────────────────────────────────────────────────────────────
    var notes: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId"           to userId,
        "opponent"         to opponent,
        "venue"            to venue,
        "sport"            to sport,
        "matchDate"        to matchDate.toString(),
        "myScore"          to myScore,
        "opponentScore"    to opponentScore,
        "goals"            to goals,
        "assists"          to assists,
        "shots"            to shots,
        "shotsOnTarget"    to shotsOnTarget,
        "bigChances"       to bigChances,
        "bigChancesMissed" to bigChancesMissed,
        "tackles"          to tackles,
        "interceptions"    to interceptions,
        "clearances"       to clearances,
        "saves"            to saves,
        "blockedShots"     to blockedShots,
        "fouls"            to fouls,
        "yellowCards"      to yellowCards,
        "redCards"         to redCards,
        "offsides"         to offsides,
        "possession"       to possession,
        "passAccuracy"     to passAccuracy,
        "totalPasses"      to totalPasses,
        "corners"          to corners,
        "dribbles"         to dribbles,
        "aerialDuelsWon"   to aerialDuelsWon,
        "rebounds"         to rebounds,
        "steals"           to steals,
        "blocks"           to blocks,
        "turnovers"        to turnovers,
        "threePointers"    to threePointers,
        "notes"            to notes,
    )

    fun toEntity(): Match = Match(
        userId           = userId,
        opponent         = opponent,
        venue            = venue,
        sport            = sport,
        matchDate        = matchDate,
        myScore          = myScore,
        opponentScore    = opponentScore,
        goals            = goals,
        assists          = assists,
        shots            = shots,
        shotsOnTarget    = shotsOnTarget,
        bigChances       = bigChances,
        bigChancesMissed = bigChancesMissed,
        tackles          = tackles,
        interceptions    = interceptions,
        clearances       = clearances,
        saves            = saves,
        blockedShots     = blockedShots,
        fouls            = fouls,
        yellowCards      = yellowCards,
        redCards         = redCards,
        offsides         = offsides,
        possession       = possession,
        passAccuracy     = passAccuracy,
        totalPasses      = totalPasses,
        corners          = corners,
        dribbles         = dribbles,
        aerialDuelsWon   = aerialDuelsWon,
        rebounds         = rebounds,
        steals           = steals,
        blocks           = blocks,
        turnovers        = turnovers,
        threePointers    = threePointers,
        notes            = notes,
        updatedAt        = Clock.System.now(),
    ).calculateResult()
}