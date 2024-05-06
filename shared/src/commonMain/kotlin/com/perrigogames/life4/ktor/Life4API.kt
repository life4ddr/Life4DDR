package com.perrigogames.life4.ktor

import com.perrigogames.life4.data.InProgressTrialSession
import kotlinx.serialization.Serializable

/**
 * API interface for obtaining core application files from Github
 */
interface Life4API {
    suspend fun getRecords(): RecordResult
}

@Serializable
data class RecordResult(
    val records: List<InProgressTrialSession>,
)
