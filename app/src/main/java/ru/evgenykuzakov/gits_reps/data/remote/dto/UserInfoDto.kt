package ru.evgenykuzakov.gits_reps.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.evgenykuzakov.gits_reps.domain.model.UserInfo

@Serializable
data class UserInfoDto(
    @SerialName("id")
    val id: Int,
    @SerialName("login")
    val login: String,
)

fun UserInfoDto.toUserInfo(): UserInfo{
    return UserInfo(login)
}