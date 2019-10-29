package mwo.lab.tapswap.api

import mwo.lab.tapswap.api.models.UserItems
import retrofit2.Call
import retrofit2.http.*


interface Endpoints {
    @POST("/items/get-user-items")
    fun getUserItems() : Call<UserItems>

}