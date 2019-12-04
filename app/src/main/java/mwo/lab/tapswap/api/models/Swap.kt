package mwo.lab.tapswap.api.models

data class Swap( //TODO: implement it, it's only here to stop IDE from screaming
    var success: Boolean?,
    var errors: List<String>?,
    var data: List<Item>?
)