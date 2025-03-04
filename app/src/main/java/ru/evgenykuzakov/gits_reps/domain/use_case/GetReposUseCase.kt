package ru.evgenykuzakov.gits_reps.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.data.remote.dto.toRepo
import ru.evgenykuzakov.gits_reps.domain.model.Repo
import ru.evgenykuzakov.gits_reps.domain.repository.AppRepository
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class GetReposUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<Resource<List<Repo>>> = flow {
        try {
            emit(Resource.Loading())
            val gitRepositories = repository.getRepositories().map { it.toRepo() }
            emit(Resource.Success(gitRepositories))
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    ErrorsDescriptionConstants.UNEXPECTED_ERROR,
                    code = e.code()
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                )
            )
        } catch (e: UnknownHostException) {
            emit(
                Resource.Error(
                    ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                )
            )
        }

    }
}