package com.perrigogames.life4.model

sealed class Result<S, E> {
    data class Success<S, E>(val data: S) : Result<S, E>()

    data class Error<S, E>(val error: E) : Result<S, E>()

    fun isSuccess() = this is Success

    fun dataOrNull(): S? = (this as? Success)?.data
}
