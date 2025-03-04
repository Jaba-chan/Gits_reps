package ru.evgenykuzakov.gits_reps.domain.repository

import ru.evgenykuzakov.gits_reps.data.remote.dto.RepoDto
import ru.evgenykuzakov.gits_reps.data.remote.dto.UserInfoDto


interface AppRepository {

    suspend fun getRepositories(): List<RepoDto>

    suspend fun getRepository(repoName: String): RepoDto

    suspend fun getRepositoryReadme(
        ownerName: String,
        repoName: String,
        branchName: String
    ): String

    suspend fun signIn(token: String): UserInfoDto
}