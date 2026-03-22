package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.entities.MatchGoal
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IMatchGoalRepository
import org.delcom.repositories.IMatchRepository
import org.delcom.repositories.IUserRepository

@Serializable
data class GoalRequest(
    val playerName: String = "",
    val minute: Int = 0,
    val isOwnGoal: Boolean = false,
    val isPenalty: Boolean = false,
    val team: String = "my",        // "my" | "opponent"
)

class MatchGoalService(
    private val userRepo: IUserRepository,
    private val matchRepo: IMatchRepository,
    private val goalRepo: IMatchGoalRepository,
) {

    // GET /matches/{id}/goals
    suspend fun getGoals(call: ApplicationCall) {
        val matchId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val match   = matchRepo.getById(matchId)

        if (match == null || match.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        val goals = goalRepo.getByMatchId(matchId)
        call.respond(DataResponse("success", "Berhasil mengambil daftar gol",
            mapOf("goals" to goals)))
    }

    // POST /matches/{id}/goals
    suspend fun postGoal(call: ApplicationCall) {
        val matchId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val match   = matchRepo.getById(matchId)

        if (match == null || match.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        val request = call.receive<GoalRequest>()

        val validator = ValidatorHelper(mapOf("playerName" to request.playerName))
        validator.required("playerName", "Nama pemain tidak boleh kosong")
        validator.validate()

        if (request.team != "my" && request.team != "opponent")
            throw AppException(400, "Team harus 'my' atau 'opponent'")

        val goalId = goalRepo.create(MatchGoal(
            matchId    = matchId,
            playerName = request.playerName,
            minute     = request.minute,
            isOwnGoal  = request.isOwnGoal,
            isPenalty  = request.isPenalty,
            team       = request.team,
        ))

        call.respond(DataResponse("success", "Berhasil menambahkan gol",
            mapOf("goalId" to goalId)))
    }

    // DELETE /matches/{id}/goals/{goalId}
    suspend fun deleteGoal(call: ApplicationCall) {
        val matchId = call.parameters["id"]     ?: throw AppException(400, "ID pertandingan tidak valid!")
        val goalId  = call.parameters["goalId"] ?: throw AppException(400, "ID gol tidak valid!")
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val match   = matchRepo.getById(matchId)

        if (match == null || match.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        val deleted = goalRepo.delete(goalId, matchId)
        if (!deleted) throw AppException(404, "Data gol tidak ditemukan!")

        call.respond(DataResponse("success", "Berhasil menghapus gol", null))
    }
}