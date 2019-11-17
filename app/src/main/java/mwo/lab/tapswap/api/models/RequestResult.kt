package mwo.lab.tapswap.api.models

data class RequestResult(
    var success: Boolean,
    var errors: List<Error>?,
    var data: Any?
)