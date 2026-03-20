package org.delcom.repositories

import org.delcom.entities.Match

interface IMatchRepository {
    /**
     * Ambil semua pertandingan user.
     * @param search  Filter nama lawan (opsional)
     * @param result  Filter hasil "W" / "L" / "D" (opsional)
     * @param sport   Filter cabang olahraga "football" / "basketball" (opsional)
     */
    suspend fun getAll(userId: String, search: String, result: String, sport: String): List<Match>

    suspend fun getById(matchId: String): Match?

    suspend fun create(match: Match): String

    suspend fun update(userId: String, matchId: String, newMatch: Match): Boolean

    suspend fun delete(userId: String, matchId: String): Boolean
}