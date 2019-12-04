package mwo.lab.tapswap.api.models

data class Myself (
    var success: Boolean,
    var data: MyselfData
) {
    data class MyselfData(
        var name: String,
        var email: String,
        var phone: String
    )
}