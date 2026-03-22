package org.delcom.module

import io.ktor.server.application.*
import org.delcom.repositories.IMatchRepository
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.repositories.IUserRepository
import org.delcom.repositories.MatchRepository
import org.delcom.repositories.RefreshTokenRepository
import org.delcom.repositories.UserRepository
import org.delcom.services.AuthService
import org.delcom.services.MatchService
import org.delcom.services.UserService
import org.koin.dsl.module

fun appModule(application: Application) = module {
    val baseUrl   = application.environment.config.property("ktor.app.baseUrl").getString()
    val jwtSecret = application.environment.config.property("ktor.jwt.secret").getString()

    // ── Repositories ──────────────────────────────────────────────────────
    single<IUserRepository>         { UserRepository(baseUrl) }
    single<IRefreshTokenRepository> { RefreshTokenRepository() }
    single<IMatchRepository>        { MatchRepository(baseUrl) }

    // ── Services ──────────────────────────────────────────────────────────
    single { AuthService(jwtSecret, get(), get()) }
    single { UserService(get(), get()) }
    single { MatchService(get(), get()) }
}