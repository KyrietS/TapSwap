package mwo.lab.tapswap.api.models

data class UserItems (
    var success: Boolean?,
    var errors: List<String>?,
    var data: List<Item>?
) {
    data class Item (
        var itemId: String?,
        var name: String?,
        var description: String?,
        var photoUrl: String?,
        var priceCategory: String?,
        var category: String?
    )
}