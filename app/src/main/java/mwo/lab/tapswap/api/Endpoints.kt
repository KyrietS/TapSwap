package mwo.lab.tapswap.api

import mwo.lab.tapswap.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface Endpoints {
    @GET("/items/get-user-items")
    fun getUserItems() : Call<UserItems>

    @GET("/items/get-rand-item")
    fun getRandItem() : Call<DiscoverItems>

    @Multipart
    @POST("/items/add")
    fun addItem(@Part file: MultipartBody.Part,
                @Part("name") name: RequestBody,
                @Part("description") desc: RequestBody,
                @Part("priceCategory") priceCat: RequestBody,
                @Part("category") category: RequestBody)
            : Call<RequestResult>

    @FormUrlEncoded
    @POST("/items/delete")
    fun deleteItem(@Field("itemId") id: Int) : Call<RequestResult>

    @FormUrlEncoded
    @POST("/users/add")
    fun addUser(@Field("name") name: String,
                @Field("email") email: String,
                @Field("phone") phone: String,
                @Field("password") password: String
    ) : Call<RequestResult>

    @FormUrlEncoded
    @POST("/users/login")
    fun login(@Field("email") email: String, @Field("password") password: String) : Call<LoginResult>

    @GET("/auth/istokenvalid")
    fun isTokenValid() : Call<RequestResult>

    @POST("/test")
    fun test() : Call<Request>
}