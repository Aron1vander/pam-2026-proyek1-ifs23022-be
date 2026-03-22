package org.delcom.repositories

import org.delcom.entities.MatchGoal

interface IMatchGoalRepository {
    suspend fun getByMatchId(matchId: String): List<MatchGoal>
    suspend fun create(goal: MatchGoal): String
    suspend fun delete(goalId: String, matchId: String): Boolean
}