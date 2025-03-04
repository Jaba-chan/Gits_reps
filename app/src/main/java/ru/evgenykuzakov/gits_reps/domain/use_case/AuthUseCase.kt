package ru.evgenykuzakov.gits_reps.domain.use_case

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.data.remote.dto.toUserInfo
import ru.evgenykuzakov.gits_reps.domain.model.UserInfo
import ru.evgenykuzakov.gits_reps.domain.repository.AppRepository
import java.io.IOException
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val appRepository: AppRepository,
) {
    operator fun invoke(token: String): Flow<Resource<UserInfo>> = flow {
        try {
            emit(Resource.Loading())
            val userInfo = appRepository.signIn(token).toUserInfo()
            emit(Resource.Success(userInfo))
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> emit(
                    Resource.Error(
                        ErrorsDescriptionConstants.INVALID_TOKEN_ERROR,
                        code = e.code()
                    )
                )

                403 -> emit(
                    Resource.Error(
                        ErrorsDescriptionConstants.ACCESS_DENIED_ERROR
                    )
                )

                else -> emit(
                    Resource.Error(
                        ErrorsDescriptionConstants.UNEXPECTED_ERROR
                    )
                )
            }
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                )
            )
        } catch (e: IllegalArgumentException){
            emit(
                Resource.Error(
                    ErrorsDescriptionConstants.INVALID_TOKEN_ERROR
                )
            )
        }
    }
}