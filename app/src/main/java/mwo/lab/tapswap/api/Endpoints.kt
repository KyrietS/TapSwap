package mwo.lab.tapswap.api

import mwo.lab.tapswap.api.models.Request
import mwo.lab.tapswap.api.models.UserItems
import retrofit2.Call
import retrofit2.http.*


interface Endpoints {
    @GET("/items/get-user-items")
    fun getUserItems() : Call<UserItems>

    @POST("/test")
    fun test() : Call<Request>
}