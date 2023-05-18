package com.mopr.menstore.api

import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.Order
import com.mopr.menstore.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface UserApiService {
    //Login API
    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<User>
    @FormUrlEncoded
    @POST("users/staff-login")
    fun staffLogin(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Call<User>

    //Signup APi
    @FormUrlEncoded
    @POST("users/register")
    fun signup(
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("email") email: String,
    ): Call<User>

    //Get user's information API
    @GET("users/{id}")
    fun getUserInfo(@Path("id") id: String): Call<User>

    @GET("users/{userId}")
    fun getUser(@Path(value = "userId", encoded = true) userId: Int): Call<User>

    @GET("users/{userId}/cart")
    fun getCart(@Path(value = "userId", encoded = true) userId: Int): Call<Cart>

    @GET("users/{id}/orders")
    fun getOrders(@Path("id") id: String): Call<List<Order>>

    @PUT("users/{id}/password")
    @FormUrlEncoded
    fun changePassword(@Path("id") id: String, @Field("old_password") oldPassword: String, @Field("new_password") newPassword: String): Call<Void>

    @PUT("users/{id}")
    @FormUrlEncoded
    fun updateUserInfo(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("birthday") birthday: String,
        @Field("email") email: String,
        @Field("image") image: String?
    ): Call<Void>

    @Multipart
    @PUT("users/{id}")
    fun uploadUserImage(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("birthday") birthday: RequestBody,
        @Part("email") email: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}