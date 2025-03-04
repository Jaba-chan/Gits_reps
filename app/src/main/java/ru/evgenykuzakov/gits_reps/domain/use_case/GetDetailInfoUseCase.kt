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
import javax.inject.Inject

class GetDetailInfoUseCase @Inject constructor(
    private val repository: AppRepository
) {
    operator fun invoke(repoName: String): Flow<Resource<Repo>> = flow {
        try {
            emit(Resource.Loading())
            val gitRepository = repository.getRepository(repoName).toRepo()
            emit(Resource.Success(gitRepository))
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    e.message ?: ErrorsDescriptionConstants.UNEXPECTED_ERROR,
                    code = e.code()
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    e.message ?: ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                )
            )
        }
    }
}
