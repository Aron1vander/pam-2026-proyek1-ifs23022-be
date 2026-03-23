package org.delcom.helpers

import io.ktor.http.content.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import java.io.File
import java.util.UUID

private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png")

/**
 * Helper untuk upload gambar dari multipart request.
 * Otomatis deteksi ekstensi dari contentType atau originalFileName.
 * Hanya menerima jpg, jpeg, png.
 */
suspend fun saveImageFromPart(part: PartData.FileItem, folder: String): String {
    val originalName = part.originalFileName ?: ""
    val extFromName  = originalName.substringAfterLast('.', "").lowercase()
    val mimeType     = part.contentType?.toString() ?: ""
    val extFromMime  = when {
        mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
        mimeType.contains("png") -> "png"
        else -> extFromName
    }
    val ext = if (extFromMime.isNotBlank()) extFromMime
    else if (extFromName.isNotBlank()) extFromName
    else "jpg"   // default fallback

    if (ext !in ALLOWED_EXTENSIONS) {
        throw AppException(400, "Format gambar tidak didukung! Gunakan JPG, JPEG, atau PNG.")
    }

    val fileName = "${UUID.randomUUID()}.$ext"
    val filePath = "$folder/$fileName"
    val file     = File(filePath).also { it.parentFile?.mkdirs() }
    part.provider().copyAndClose(file.writeChannel())

    if (!file.exists()) throw AppException(400, "Gambar gagal diunggah!")
    return filePath
}