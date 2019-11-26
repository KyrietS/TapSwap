package mwo.lab.tapswap.api.models

data class LoginResult(
    val success: Boolean,
    val errors: List<Error>?,
    val data: DataToken?
) {
    data class DataToken(
        val userToken: String
    )
}