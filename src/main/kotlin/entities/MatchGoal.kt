package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MatchGoal(
    var id: String = UUID.randomUUID().toString(),
    var matchId: String,
    var playerName: String,
    var minute: Int = 0,
    var isOwnGoal: Boolean = false,
    var isPenalty: Boolean = false,
    var team: String = "my",   // "my" | "opponent"
    @Contextual
    val createdAt: Instant = Clock.System.now(),
)