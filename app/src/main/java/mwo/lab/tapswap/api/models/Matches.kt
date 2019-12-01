package mwo.lab.tapswap.api.models

data class Matches(
    var success: Boolean?,
    var errors: List<String>?,
    var data: MatchesData?
) {
    class MatchesData (
        var matches: List<Match>
    ) {
        class Match (
            var id: Int,
            var myItem: MatchesItem,
            var exchangeItem: MatchesItem,
            var toWho: MatchesWho,
            var fromWho: MatchesWho
        )

        class MatchesItem (
            var id: Int,
            var name: String,
            var description: String,
            var photoUrl: String,
            var priceCategory: String,
            var category: String
        )

        class MatchesWho(
            var id: Int,
            var name: String,
            var email: String,
            var phone: String
        )
    }
}