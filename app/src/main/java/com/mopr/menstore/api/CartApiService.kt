package com.mopr.menstore.api

import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.CartItem
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApiService {
	@GET("carts/{cartId}")
	fun getCart(@Path(value = "cartId", encoded = true) cartId: Int): Call<Cart>

	@GET("carts/{cartId}/items")
	fun getCartItems(@Path(value = "cartId", encoded = true) cartId: Int): Call<List<CartItem>>

	@FormUrlEncoded
	@POST("carts/{cartId}/items")
	fun addToCart(
		@Path(value = "cartId", encoded = true) cartId: Int,
		@Field("product_detail_id") productDetailId: Int,
		@Field("quantity") quantity: Int
	): Call<CartItem>
}