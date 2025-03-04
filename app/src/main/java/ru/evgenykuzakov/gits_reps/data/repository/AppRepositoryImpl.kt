package ru.evgenykuzakov.gits_reps.data.repository

import ru.evgenykuzakov.gits_reps.data.remote.GitHubApi
import ru.evgenykuzakov.gits_reps.data.remote.dto.RepoDto
import ru.evgenykuzakov.gits_reps.data.remote.dto.UserInfoDto
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val api: GitHubApi,
    private val keyValueStorage: KeyValueStorage
) : AppRepository {

    private var cachedAuthToken: String? = null

    override suspend fun getRepositories(): List<RepoDto> {
        return api.getRepositories(getCachedAuthToken())
    }

    override suspend fun getRepository(repoName: String): RepoDto {
        return getRepositories().first { it.name == repoName }
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repoName: String,
        branchName: String
    ): String {
        return api.getRepositoryReadme(
            token = getCachedAuthToken(),
            owner = ownerName,
            repo = repoName,
            branchName = branchName
        ).content
    }

    override suspend fun signIn(token: String): UserInfoDto {
        cachedAuthToken = "token $token"
        return api.signIn(cachedAuthToken!!)
    }

    private fun getCachedAuthToken(): String {
        return cachedAuthToken ?: ("token ${keyValueStorage.authToken ?: ""}"
        ).also { cachedAuthToken = it }
    }
}