package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.MatchRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IMatchRepository
import org.delcom.repositories.IUserRepository

class MatchService(
    private val userRepo: IUserRepository,
    private val matchRepo: IMatchRepository,
) {

    // GET /matches?search=&result=W&sport=football
    suspend fun getAll(call: ApplicationCall) {
        val user   = ServiceHelper.getAuthUser(call, userRepo)
        val search = call.request.queryParameters["search"] ?: ""
        val result = call.request.queryParameters["result"] ?: ""   // "W" / "L" / "D"
        val sport  = call.request.queryParameters["sport"]  ?: ""   // "football" / "basketball"

        val matches = matchRepo.getAll(user.id, search, result, sport)

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil daftar pertandingan",
                mapOf("matches" to matches)
            )
        )
    }

    // GET /matches/{id}
    suspend fun getById(call: ApplicationCall) {
        val matchId = call.parameters["id"]
            ?: throw AppException(400, "ID pertandingan tidak valid!")

        val user  = ServiceHelper.getAuthUser(call, userRepo)
        val match = matchRepo.getById(matchId)

        if (match == null || match.userId != user.id) {
            throw AppException(404, "Data pertandingan tidak ditemukan!")
        }

        call.respond(
            DataResponse(
                "success",
                "Berhasil mengambil detail pertandingan",
                mapOf("match" to match)
            )
        )
    }

    // POST /matches
    suspend fun post(call: ApplicationCall) {
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<MatchRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("opponent", "Nama lawan tidak boleh kosong")
        validator.required("matchDate", "Tanggal pertandingan tidak boleh kosong")
        validator.validate()

        val matchId = matchRepo.create(request.toEntity())

        call.respond(
            DataResponse(
                "success",
                "Berhasil menambahkan data pertandingan",
                mapOf("matchId" to matchId)
            )
        )
    }

    // PUT /matches/{id}
    suspend fun put(call: ApplicationCall) {
        val matchId = call.parameters["id"]
            ?: throw AppException(400, "ID pertandingan tidak valid!")

        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<MatchRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("opponent", "Nama lawan tidak boleh kosong")
        validator.required("matchDate", "Tanggal pertandingan tidak boleh kosong")
        validator.validate()

        val existing = matchRepo.getById(matchId)
        if (existing == null || existing.userId != user.id) {
            throw AppException(404, "Data pertandingan tidak ditemukan!")
        }

        val isUpdated = matchRepo.update(user.id, matchId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data pertandingan!")

        call.respond(DataResponse("success", "Berhasil mengubah data pertandingan", null))
    }

    // DELETE /matches/{id}
    suspend fun delete(call: ApplicationCall) {
        val matchId = call.parameters["id"]
            ?: throw AppException(400, "ID pertandingan tidak valid!")

        val user     = ServiceHelper.getAuthUser(call, userRepo)
        val existing = matchRepo.getById(matchId)

        if (existing == null || existing.userId != user.id) {
            throw AppException(404, "Data pertandingan tidak ditemukan!")
        }

        val isDeleted = matchRepo.delete(user.id, matchId)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data pertandingan!")

        call.respond(DataResponse("success", "Berhasil menghapus data pertandingan", null))
    }
}