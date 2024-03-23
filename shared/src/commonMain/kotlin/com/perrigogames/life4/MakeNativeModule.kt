package com.perrigogames.life4

import com.perrigogames.life4.api.base.LocalDataReader
import com.perrigogames.life4.api.base.LocalUncachedDataReader
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.IGNORES_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.MOTD_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PARTIAL_DIFFICULTY_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.PLACEMENTS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.RANKS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.SONGS_FILE_NAME
import com.perrigogames.life4.ktor.GithubDataAPI.Companion.TRIALS_FILE_NAME
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun makeNativeModule(
    ignoresDataReader: LocalDataReader
): Module {
    return module {
        single<LocalDataReader>(named(IGNORES_FILE_NAME)) { ignoresDataReader }
    }
}