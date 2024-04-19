package com.benyq.tikbili.base.ui

/**
 *
 * @author benyq
 * @date 12/12/2023
 *
 */
sealed class DataState<T> {
    data class Success<T>(val data: T) : DataState<T>()
    data class Error<T>(val error: Throwable?) : DataState<T>()
    data class Loading<T>(val loading: Boolean = false) : DataState<T>()
    inline fun <R> map(block: (T) -> R): DataState<R> {
        return when(this) {
            is Success -> {
                Success(block(data))
            }
            is Error -> {
                Error(error)
            }
            is Loading -> {
                Loading(loading)
            }
        }
    }
}