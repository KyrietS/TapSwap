package mwo.lab.tapswap.api.models

data class UserItems (
    var success: Boolean?,
    var errors: List<String>?,
    var data: List<Item>?
)