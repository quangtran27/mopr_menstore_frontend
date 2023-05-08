package com.mopr.menstore.api

import com.mopr.menstore.models.ListResponse
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.models.Review
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

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
}