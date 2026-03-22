package org.delcom.repositories

import org.delcom.entities.Match

interface IMatchRepository {
    suspend fun getAll(userId: String, search: String, result: String, sport: String): List<Match>
    suspend fun getById(matchId: String): Match?
    suspend fun create(match: Match): String
    suspend fun update(userId: String, matchId: String, newMatch: Match): Boolean
    suspend fun updateLogo(userId: String, matchId: String, logoPath: String): Boolean
    suspend fun updateMyLogo(userId: String, matchId: String, logoPath: String): Boolean  // ← tambah
    suspend fun delete(userId: String, matchId: String): Boolean
}