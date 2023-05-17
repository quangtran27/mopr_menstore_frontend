package com.mopr.menstore.api

import com.mopr.menstore.models.Order
import com.mopr.menstore.models.OrderItem
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface OrderApiService {
    @GET("users/{user_id}/orders")
    fun getOrdersByUser(@Path(value = "user_id", encoded = true)userId: Int): Call<List<Order>>

    @GET("orders/{order_id}/items")
    fun getOrderItems(@Path(value = "order_id", encoded = true)orderId: Int): Call<List<OrderItem>>

    @GET("orders/{order_id}")
    fun getOrder(@Path(value = "order_id", encoded = true)orderId: Int): Call<Order>

    @FormUrlEncoded
    @PUT("orders/{order_id}")
    fun updateOrder(
        @Path(value = "order_id", encoded = true)orderId: Int,
        @Field("status") status: Int,
        @Field("is_paid") isPaid: Int,
        @Field("is_reviewed") isReviewed: Int
        ): Call<RequestBody>

    @FormUrlEncoded
    @POST("orders/")
    fun addOrder(
        @Field("user_id") user_id: Int,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("payment") payment: Int,
        @Field("cart_item_ids") cart_item_ids: String,
        @Field("note") note: String
    ):Call<RequestBody>
}