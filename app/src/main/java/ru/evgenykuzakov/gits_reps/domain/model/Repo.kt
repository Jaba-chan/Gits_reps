package ru.evgenykuzakov.gits_reps.domain.model

data class Repo(
    val owner: String,
    val name: String,
    val description: String,
    val language: String?,
    val link: String,
    val licenseHeading: String?,
    val defaultBranch: String,
    val starsCount: Int?,
    val forksCount: Int?,
    val watchersCount: Int?,
)