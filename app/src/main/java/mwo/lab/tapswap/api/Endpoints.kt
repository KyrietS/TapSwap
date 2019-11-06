package mwo.lab.tapswap.api

import mwo.lab.tapswap.api.models.Request
import mwo.lab.tapswap.api.models.RequestResult
import mwo.lab.tapswap.api.models.UserItems
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface Endpoints {
    @GET("/items/get-user-items")
    fun getUserItems() : Call<UserItems>

    @Multipart
    @POST("/items/add")
    fun addItem(@Part file: MultipartBody.Part,
                @Part("name") name: String,
                @Part("description") desc: String,
                @Part("priceCategory") priceCat: String,
                @Part("category") category: String)
            : Call<RequestResult>

    @FormUrlEncoded
    @POST("/items/delete")
    fun deleteItem(@Field("itemId") id: Int) : Call<RequestResult>

    @POST("/test")
    fun test() : Call<Request>
}