package ru.evgenykuzakov.gits_reps.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import ru.evgenykuzakov.gits_reps.domain.model.Repo

@Serializable
data class RepoDto(
    @SerializedName("owner") val owner: OwnerDto,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("html_url") val link: String,
    @SerializedName("license") val license: LicenseDto?,
    @SerializedName("default_branch") val defaultBranch: String?,
    @SerializedName("stargazers_count") val starsCount: Int?,
    @SerializedName("forks_count") val forksCount: Int?,
    @SerializedName("watchers_count") val watchersCount: Int?
)

fun RepoDto.toRepo(): Repo {
    return Repo(
        owner = owner.login,
        name = name,
        description = description ?: "",
        language = language,
        link = link,
        licenseHeading = license?.spdxId ?: "",
        defaultBranch = defaultBranch ?: "",
        starsCount = starsCount,
        forksCount = forksCount,
        watchersCount = watchersCount
    )
}