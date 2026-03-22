package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.AuthService
import org.delcom.services.MatchService
import org.delcom.services.UserService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService: AuthService   by inject()
    val userService: UserService   by inject()
    val matchService: MatchService by inject()

    install(StatusPages) {
        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)
            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status  = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data    = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.fromValue(500),
                message = ErrorResponse(status = "error", message = cause.message ?: "Unknown error", data = "")
            )
        }
    }

    routing {
        get("/") { call.respondText("Match Statistics Log (MatchUp) API by Aron Ivander  — berjalan normal.") }

        route("/auth") {
            post("/register")      { authService.postRegister(call) }
            post("/login")         { authService.postLogin(call) }
            post("/refresh-token") { authService.postRefreshToken(call) }
            post("/logout")        { authService.postLogout(call) }
        }

        authenticate(JWTConstants.NAME) {
            route("/users") {
                get("/me")              { userService.getMe(call) }
                put("/me")              { userService.putMe(call) }
                put("/me/password")     { userService.putMyPassword(call) }
                put("/me/photo")        { userService.putMyPhoto(call) }
                put("/me/team-logo")    { userService.putMyTeamLogo(call) }  // ← logo tim global
            }

            route("/matches") {
                get              { matchService.getAll(call) }
                post             { matchService.post(call) }
                get("/{id}")     { matchService.getById(call) }
                put("/{id}")     { matchService.put(call) }
                put("/{id}/logo")    { matchService.putLogo(call) }      // logo lawan
                put("/{id}/my-logo") { matchService.putMyLogo(call) }   // ← logo tim per match
                delete("/{id}")  { matchService.delete(call) }
            }
        }

        route("/images") {
            get("/users/{id}") { userService.getPhoto(call) }
        }
    }
}