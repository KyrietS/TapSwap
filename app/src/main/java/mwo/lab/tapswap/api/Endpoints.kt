package mwo.lab.tapswap.api

import mwo.lab.tapswap.api.models.Request
import mwo.lab.tapswap.api.models.UserItems
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface Endpoints {
    @GET("/items/get-user-items")
    fun getUserItems() : Call<UserItems>

    @Multipart
    @POST("/items/add")
    fun addItem(@Part file: MultipartBody.Part) : Call<Any>

    @POST("/test")
    fun test() : Call<Request>
}