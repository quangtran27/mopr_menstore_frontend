package com.mopr.menstore.api

import com.mopr.menstore.models.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.Date

interface UserApiService {
    //Login API
    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ):retrofit2.Call<User>

    //Signup APi
    @FormUrlEncoded
    @POST("users/register")
    fun sigup(
        @Field("phone") phone: String,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("email") email: String,
    ):retrofit2.Call<User>
}