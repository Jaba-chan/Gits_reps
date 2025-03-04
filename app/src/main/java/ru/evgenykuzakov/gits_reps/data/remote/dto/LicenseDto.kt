package ru.evgenykuzakov.gits_reps.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LicenseDto(
    @SerialName("spdx_id") @SerializedName("spdx_id") val spdxId: String
)