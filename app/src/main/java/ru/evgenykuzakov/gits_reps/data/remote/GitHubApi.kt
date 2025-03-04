package ru.evgenykuzakov.gits_reps.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import ru.evgenykuzakov.gits_reps.data.remote.dto.ReadmeFileDto
import ru.evgenykuzakov.gits_reps.data.remote.dto.RepoDto
import ru.evgenykuzakov.gits_reps.data.remote.dto.UserInfoDto

interface GitHubApi {

    @GET("user/repos")
    suspend fun getRepositories(@Header("Authorization") token: String): List<RepoDto>

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepositoryReadme(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("ref") branchName: String
    ): ReadmeFileDto

    @GET("user")
    suspend fun signIn(
        @Header("Authorization") token: String
    ): UserInfoDto

}