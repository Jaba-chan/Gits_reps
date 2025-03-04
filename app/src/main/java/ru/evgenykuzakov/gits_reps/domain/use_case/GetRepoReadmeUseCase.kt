package ru.evgenykuzakov.gits_reps.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.domain.repository.AppRepository
import java.io.IOException
import javax.inject.Inject

class GetRepoReadmeUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(
        owner: String,
        repo: String,
        branchName: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val repositoryReadme = repository.getRepositoryReadme(
                repoName = repo,
                ownerName = owner,
                branchName = branchName,
            )
            emit(Resource.Success(repositoryReadme))
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> emit(
                    Resource.Error(
                        e.message ?: ErrorsDescriptionConstants.NOT_FOUND_ERROR,
                        code = e.code()
                    )
                )

                else -> emit(
                    Resource.Error(
                        e.message ?: ErrorsDescriptionConstants.UNEXPECTED_ERROR,
                        code = e.code()
                    )
                )
            }

        } catch (e: IOException) {
            emit(
                Resource.Error(
                    e.message ?: ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                )
            )
        }
    }
}