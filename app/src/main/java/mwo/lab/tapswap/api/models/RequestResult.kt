package mwo.lab.tapswap.api.models

data class RequestResult(
    private var _success: String?,
    var errors: List<Error>?,
    var data: Any?
) {
    val success: Boolean
        get() = _success=="true"

    data class Error(
        var message: String?,
        var code: String?
    )
}