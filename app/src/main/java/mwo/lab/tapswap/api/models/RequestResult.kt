package mwo.lab.tapswap.api.models

data class RequestResult(
    private var success: String?,
    var errors: List<Error>?,
    var data: Any?
) {
    val isSuccess: Boolean
        get() = success=="true"

    data class Error(
        var message: String?,
        var code: String?
    )
}