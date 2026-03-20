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

    // ── Info Umum ──────────────────────────────────────────────────────────
    var opponent: String,
    var venue: String = "",                 // Lokasi pertandingan (opsional)
    var sport: String = "football",         // "football" | "basketball"

    @Contextual
    var matchDate: LocalDate,

    var myScore: Int = 0,
    var opponentScore: Int = 0,
    var result: String = "",                // "W" / "L" / "D" — dihitung otomatis

    // ── Statistik Serangan ─────────────────────────────────────────────────
    var goals: Int = 0,                     // Gol / poin yang dicetak
    var assists: Int = 0,                   // Assist
    var shots: Int = 0,                     // Total tembakan
    var shotsOnTarget: Int = 0,             // Tembakan tepat sasaran
    var bigChances: Int = 0,                // Peluang emas
    var bigChancesMissed: Int = 0,          // Peluang emas terbuang

    // ── Statistik Bertahan ─────────────────────────────────────────────────
    var tackles: Int = 0,                   // Tekel
    var interceptions: Int = 0,            // Intersep
    var clearances: Int = 0,               // Sapuan bola
    var saves: Int = 0,                    // Penyelamatan kiper
    var blockedShots: Int = 0,             // Tembakan yang diblok

    // ── Disiplin ───────────────────────────────────────────────────────────
    var fouls: Int = 0,                    // Pelanggaran
    var yellowCards: Int = 0,             // Kartu kuning
    var redCards: Int = 0,                // Kartu merah
    var offsides: Int = 0,               // Offside

    // ── Penguasaan & Passing ───────────────────────────────────────────────
    var possession: Int = 0,               // Penguasaan bola (0–100 %)
    var passAccuracy: Int = 0,             // Akurasi umpan (0–100 %)
    var totalPasses: Int = 0,              // Total umpan
    var corners: Int = 0,                  // Tendangan sudut
    var dribbles: Int = 0,                 // Dribel berhasil
    var aerialDuelsWon: Int = 0,           // Duel udara menang

    // ── Khusus Basketball ─────────────────────────────────────────────────
    var rebounds: Int = 0,                 // Rebound
    var steals: Int = 0,                   // Steal
    var blocks: Int = 0,                   // Blok
    var turnovers: Int = 0,                // Turnover
    var threePointers: Int = 0,            // Tembakan 3 poin masuk

    // ── Catatan ────────────────────────────────────────────────────────────
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