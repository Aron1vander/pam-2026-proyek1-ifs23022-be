package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.content.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.MatchRequest
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IMatchRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.UUID

class MatchService(
    private val userRepo: IUserRepository,
    private val matchRepo: IMatchRepository,
) {

    suspend fun getAll(call: ApplicationCall) {
        val user   = ServiceHelper.getAuthUser(call, userRepo)
        val search = call.request.queryParameters["search"] ?: ""
        val result = call.request.queryParameters["result"] ?: ""
        val sport  = call.request.queryParameters["sport"]  ?: ""
        val matches = matchRepo.getAll(user.id, search, result, sport)
        call.respond(DataResponse("success", "Berhasil mengambil daftar pertandingan",
            mapOf("matches" to matches)))
    }

    suspend fun getById(call: ApplicationCall) {
        val matchId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user  = ServiceHelper.getAuthUser(call, userRepo)
        val match = matchRepo.getById(matchId)
        if (match == null || match.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")
        call.respond(DataResponse("success", "Berhasil mengambil detail pertandingan",
            mapOf("match" to match)))
    }

    suspend fun post(call: ApplicationCall) {
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<MatchRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("opponent", "Nama lawan tidak boleh kosong")
        validator.required("matchDate", "Tanggal tidak boleh kosong")
        validator.validate()

        val matchId = matchRepo.create(request.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan data pertandingan",
            mapOf("matchId" to matchId)))
    }

    suspend fun put(call: ApplicationCall) {
        val matchId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<MatchRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("opponent", "Nama lawan tidak boleh kosong")
        validator.required("matchDate", "Tanggal tidak boleh kosong")
        validator.validate()

        val existing = matchRepo.getById(matchId)
        if (existing == null || existing.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        val isUpdated = matchRepo.update(user.id, matchId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data pertandingan!")
        call.respond(DataResponse("success", "Berhasil mengubah data pertandingan", null))
    }

    // Upload logo lawan
    suspend fun putLogo(call: ApplicationCall) {
        val matchId = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val existing = matchRepo.getById(matchId)
        if (existing == null || existing.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        var newLogoPath: String? = null
        call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5).forEachPart { part ->
            if (part is PartData.FileItem) {
                val ext = part.originalFileName?.substringAfterLast('.', "")
                    ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                val filePath = "uploads/logos/${UUID.randomUUID()}$ext"
                val file = File(filePath).also { it.parentFile.mkdirs() }
                part.provider().copyAndClose(file.writeChannel())
                newLogoPath = filePath
            }
            part.dispose()
        }

        if (newLogoPath == null) throw AppException(400, "Logo tidak tersedia!")
        if (!File(newLogoPath!!).exists()) throw AppException(400, "Logo gagal diunggah!")

        existing.opponentLogo?.let { if (File(it).exists()) File(it).delete() }

        val isUpdated = matchRepo.updateLogo(user.id, matchId, newLogoPath!!)
        if (!isUpdated) throw AppException(400, "Gagal menyimpan logo!")
        call.respond(DataResponse("success", "Berhasil mengubah logo lawan", null))
    }

    // ← Upload logo tim sendiri per pertandingan (override logo global)
    suspend fun putMyLogo(call: ApplicationCall) {
        val matchId  = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user     = ServiceHelper.getAuthUser(call, userRepo)
        val existing = matchRepo.getById(matchId)
        if (existing == null || existing.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        var newLogoPath: String? = null
        call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5).forEachPart { part ->
            if (part is PartData.FileItem) {
                val ext = part.originalFileName?.substringAfterLast('.', "")
                    ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                val filePath = "uploads/my-logos/${UUID.randomUUID()}$ext"
                val file = File(filePath).also { it.parentFile.mkdirs() }
                part.provider().copyAndClose(file.writeChannel())
                newLogoPath = filePath
            }
            part.dispose()
        }

        if (newLogoPath == null) throw AppException(400, "Logo tidak tersedia!")
        if (!File(newLogoPath!!).exists()) throw AppException(400, "Logo gagal diunggah!")

        existing.myLogo?.let { if (File(it).exists()) File(it).delete() }

        val isUpdated = matchRepo.updateMyLogo(user.id, matchId, newLogoPath!!)
        if (!isUpdated) throw AppException(400, "Gagal menyimpan logo tim!")
        call.respond(DataResponse("success", "Berhasil mengubah logo tim", null))
    }

    suspend fun delete(call: ApplicationCall) {
        val matchId  = call.parameters["id"] ?: throw AppException(400, "ID tidak valid!")
        val user     = ServiceHelper.getAuthUser(call, userRepo)
        val existing = matchRepo.getById(matchId)
        if (existing == null || existing.userId != user.id)
            throw AppException(404, "Data pertandingan tidak ditemukan!")

        val isDeleted = matchRepo.delete(user.id, matchId)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data pertandingan!")

        existing.opponentLogo?.let { if (File(it).exists()) File(it).delete() }
        existing.myLogo?.let { if (File(it).exists()) File(it).delete() }

        call.respond(DataResponse("success", "Berhasil menghapus data pertandingan", null))
    }
}