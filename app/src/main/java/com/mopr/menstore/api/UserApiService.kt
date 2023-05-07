package com.mopr.menstore.api

import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {
	@GET("users/{userId}")
	fun getUser(@Path(value="userId", encoded = true) userId: Int): Call<User>

	@GET("users/{userId}/cart")
	fun getCart(@Path(value="userId", encoded = true) userId: Int): Call<Cart>
}