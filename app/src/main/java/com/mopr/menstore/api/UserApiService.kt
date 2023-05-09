package com.mopr.menstore.api

import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.http.*
import java.util.Date

interface UserApiService {
    //Login API
    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ):Call<User>

    //Signup APi
    @FormUrlEncoded
    @POST("users/register")
    fun signup(
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("email") email: String,
    ):Call<User>

    //Get user's information API
    @GET("users/{id}")
    fun getUserInfo(@Path("id") id: String): Call<User>

    //Change user's password API
    @FormUrlEncoded
    @POST("users/{id}/password")
    fun changePassword(@Path("id") id: String, @Field("old_password") oldPassword: String, @Field("new_password") newPassword: String): Call<Void>
}