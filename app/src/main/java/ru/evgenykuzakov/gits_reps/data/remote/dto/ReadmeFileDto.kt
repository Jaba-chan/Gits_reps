package ru.evgenykuzakov.gits_reps.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeFileDto(
    @SerialName("content") @SerializedName("content") val content: String,
)


