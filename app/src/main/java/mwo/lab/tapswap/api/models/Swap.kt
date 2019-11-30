package mwo.lab.tapswap.api.models

data class Swap( //TODO: FIX IT
    var success: Boolean?,
    var errors: List<String>?,
    var data: List<Item>?
)