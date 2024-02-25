package ru.gidevent.androidapp.network

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(
        val code: Int,
        val body: String,
        val exception: Exception? = null
    ) : ApiResult<Nothing>() {
        private var description: String = when (code) {
            0 -> body
            200 -> body
            201 -> body
            202 -> body
            404 -> "ERROR - Not found"
            403 -> "ERROR - Forbidden"
            401 -> "ERROR - Unauthorized"
            400 -> "ERROR - Bad request"
            500 -> "ERROR - Internal Server Error"
            else -> "ERROR - Unknown error $code"

        }


        fun errorStringFormatLong(): String {
            return "code = $code, " +
                    "description = $description, " +
                    "body = $body, " +
                    "exception = ${exception?.message}"
        }
    }
}