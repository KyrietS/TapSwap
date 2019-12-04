package mwo.lab.tapswap.api.models

data class UserSwaps (
    var success: Boolean?,
    var errors: List<String>?,
    var data: List<Swap>?
)