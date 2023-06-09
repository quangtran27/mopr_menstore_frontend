package com.mopr.menstore.api

import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.CartItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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

	@PUT("carts/{cartId}/items/{cartItemId}")
	@FormUrlEncoded
	fun updateCartItem(
		@Path(value = "cartId", encoded = true) cartId: Int,
		@Path(value = "cartItemId", encoded = true) cartItemId: Int,
		@Field("quantity") quantity: Int
	): Call<CartItem>

	@DELETE("carts/{cartId}/items/{cartItemId}")
	fun deleteCartItem(
		@Path(value = "cartId", encoded = true) cartId: Int,
		@Path(value = "cartItemId", encoded = true) cartItemId: Int,
	): Call<ResponseBody>
}