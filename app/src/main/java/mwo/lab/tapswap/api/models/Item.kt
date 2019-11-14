package mwo.lab.tapswap.api.models

data class Item (
    var id: Int,
    var itemName: String?,
    var itemDescription: String?,
    var itemPhoto: String?,
    var itemPriceCategory: String?,
    var itemCategory: String?,
    var itemStatus: String?
)