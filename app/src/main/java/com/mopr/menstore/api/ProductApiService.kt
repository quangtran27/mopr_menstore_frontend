package com.mopr.menstore.api

import com.mopr.menstore.models.*
import retrofit2.Call
import retrofit2.http.*

interface ProductApiService {
	@GET("products/")
	fun getAll(
		@QueryMap options: Map<String, String>
	): Call<ListResponse<Product>>

	@GET("products/{productId}")
	fun get(@Path(value = "productId", encoded = true) productId: Int): Call<Product>

	@GET("products/{productId}/details")
	fun getDetails(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<ProductDetail>>

	@GET("products/{productId}/images")
	fun getImages(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<ProductImage>>

	@GET("products/{productId}/reviews")
	fun getReviews(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<Review>>

	@GET("products/top-sales")
	fun getTopSale(): Call<List<Product>>

	@GET("products/latest")
	fun getLatest(): Call<List<Product>>

	@GET("products/details/{product_detail_id}")
	fun getProductDetail(@Path(value = "product_detail_id", encoded = true)productDetailId: Int): Call<ProductDetail>
	@GET("products/{product_id}")
	fun getProduct(@Path(value = "product_id", encoded = true)productId: Int): Call<Product>
	@GET("products/{product_id}/images")
	fun getImagesByProduct(@Path(value = "product_id", encoded = true)productId: Int): Call<List<ProductImage>>
}