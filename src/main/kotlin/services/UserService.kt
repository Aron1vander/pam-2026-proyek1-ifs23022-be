package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.AuthRequest
import org.delcom.data.DataResponse
import org.delcom.data.UserResponse
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

// Format gambar yang diizinkan
private val ALLOWED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png")
private val ALLOWED_IMAGE_MIME_TYPES = setOf("image/jpeg", "image/jpg", "image/png")

class UserService(
    private val userRepo: IUserRepository,
    private val refreshTokenRepo: IRefreshTokenRepository,
) {

    suspend fun getMe(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        call.respond(DataResponse("success", "Berhasil mengambil informasi akun saya",
            mapOf("user" to UserResponse(
                id          = user.id,
                name        = user.name,
                username    = user.username,
                urlPhoto    = user.urlPhoto,
                urlTeamLogo = user.urlTeamLogo,
                createdAt   = user.createdAt,
                updatedAt   = user.updatedAt,
            ))
        ))
    }

    suspend fun putMe(call: ApplicationCall) {
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("name",     "Nama tidak boleh kosong")
        validator.required("username", "Username tidak boleh kosong")
        validator.validate()

        val existUser = userRepo.getByUsername(request.username)
        if (existUser != null && existUser.username != user.username) {
            throw AppException(409, "Akun dengan username ini sudah terdaftar!")
        }

        user.username = request.username
        user.name     = request.name
        val isUpdated = userRepo.update(user.id, user)
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data profile!")

        call.respond(DataResponse("success", "Berhasil mengubah data profile", null))
    }

    suspend fun putMyPhoto(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        var newPhoto: String? = null

        call.receiveMultipart(formFieldLimit = 1024 * 1024 * 10).forEachPart { part ->
            if (part is PartData.FileItem) {
                // Ambil ekstensi dari originalFileName atau contentType
                val originalName = part.originalFileName ?: ""
                val extFromName  = originalName.substringAfterLast('.', "").lowercase()
                val mimeType     = part.contentType?.toString() ?: ""
                val extFromMime  = when {
                    mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
                    mimeType.contains("png")  -> "png"
                    else -> extFromName
                }
                val ext = if (extFromMime.isNotBlank()) extFromMime else extFromName

                // Validasi format
                if (ext.isBlank() || ext !in ALLOWED_IMAGE_EXTENSIONS) {
                    part.dispose()
                    throw AppException(400, "Format gambar tidak didukung! Gunakan JPG, JPEG, atau PNG.")
                }

                val fileName = "${UUID.randomUUID()}.$ext"
                val filePath = "uploads/users/$fileName"
                val file     = File(filePath).also { it.parentFile?.mkdirs() }
                part.provider().copyAndClose(file.writeChannel())
                newPhoto = filePath
            }
            part.dispose()
        }

        if (newPhoto == null) throw AppException(400, "Foto profil tidak tersedia!")
        if (!File(newPhoto!!).exists()) throw AppException(400, "Foto profil gagal diunggah!")

        val oldPhoto = user.photo
        user.photo   = newPhoto
        val isUpdated = userRepo.update(user.id, user)
        if (!isUpdated) throw AppException(400, "Gagal memperbarui foto profil!")

        if (oldPhoto != null) {
            val oldFile = File(oldPhoto)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse("success", "Berhasil mengubah foto profil", null))
    }

    suspend fun putMyTeamLogo(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)
        var newLogoPath: String? = null

        call.receiveMultipart(formFieldLimit = 1024 * 1024 * 10).forEachPart { part ->
            if (part is PartData.FileItem) {
                val originalName = part.originalFileName ?: ""
                val extFromName  = originalName.substringAfterLast('.', "").lowercase()
                val mimeType     = part.contentType?.toString() ?: ""
                val extFromMime  = when {
                    mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
                    mimeType.contains("png")  -> "png"
                    else -> extFromName
                }
                val ext = if (extFromMime.isNotBlank()) extFromMime else extFromName

                if (ext.isBlank() || ext !in ALLOWED_IMAGE_EXTENSIONS) {
                    part.dispose()
                    throw AppException(400, "Format gambar tidak didukung! Gunakan JPG, JPEG, atau PNG.")
                }

                val fileName = "${UUID.randomUUID()}.$ext"
                val filePath = "uploads/team-logos/$fileName"
                val file     = File(filePath).also { it.parentFile?.mkdirs() }
                part.provider().copyAndClose(file.writeChannel())
                newLogoPath = filePath
            }
            part.dispose()
        }

        if (newLogoPath == null) throw AppException(400, "Logo tidak tersedia!")
        if (!File(newLogoPath!!).exists()) throw AppException(400, "Logo gagal diunggah!")

        val oldLogo  = user.teamLogo
        user.teamLogo = newLogoPath
        val isUpdated = userRepo.update(user.id, user)
        if (!isUpdated) throw AppException(400, "Gagal menyimpan logo tim!")

        if (oldLogo != null) {
            val oldFile = File(oldLogo)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse("success", "Berhasil mengubah logo tim", null))
    }

    suspend fun putMyPassword(call: ApplicationCall) {
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("newPassword", "Kata sandi baru tidak boleh kosong")
        validator.required("password",    "Kata sandi lama tidak boleh kosong")
        validator.validate()

        val validPassword = verifyPassword(request.password, user.password)
        if (!validPassword) throw AppException(404, "Kata sandi lama tidak valid!")

        user.password = hashPassword(request.newPassword)
        val isUpdated = userRepo.update(user.id, user)
        if (!isUpdated) throw AppException(400, "Gagal mengubah kata sandi!")

        refreshTokenRepo.deleteByUserId(user.id)
        call.respond(DataResponse("success", "Berhasil mengubah kata sandi", null))
    }

    suspend fun getPhoto(call: ApplicationCall) {
        val userId = call.parameters["id"]
            ?: throw AppException(400, "Data user tidak valid!")
        val user = userRepo.getById(userId)
            ?: throw AppException(400, "User not found!")

        if (user.photo == null) throw AppException(404, "User belum memiliki foto profil")
        val file = File(user.photo!!)
        if (!file.exists()) throw AppException(404, "Foto profil tidak tersedia")
        call.respondFile(file)
    }
}