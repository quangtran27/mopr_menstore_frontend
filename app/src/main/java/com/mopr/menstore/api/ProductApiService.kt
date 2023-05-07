package com.mopr.menstore.api

import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.models.Review
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
	@GET("products/")
	fun getAllProducts(
		@Query("page") page: Int = 1,
		@Query("sort_by") sortBy: String = "",
		@Query("order") order: String = "asc",
	): Call<List<Product>>

	@GET("products/{productId}")
	fun getProduct(@Path(value = "productId", encoded = true) productId: Int): Call<Product>

	@GET("products/{productId}/details")
	fun getProductDetails(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<ProductDetail>>

	@GET("products/{productId}/images")
	fun getProductImages(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<ProductImage>>

	@GET("products/{productId}/reviews")
	fun getProductReviews(
		@Path(
			value = "productId",
			encoded = true
		) productId: Int
	): Call<List<Review>>

	@GET("products/top-sales")
	fun getTopSaleProduct(): Call<List<Product>>

	@GET("products/latest")
	fun getLatestProducts(): Call<List<Product>>

	@GET("products/search")
	fun searchProducts(
		@Query("keyword") keyword: String = "",
		@Query("page") page: Int = 1,
		@Query("sort_by") sortBy: String = "",
		@Query("order") order: String = "asc",
		@Query("category_id") categoryId: Int = 0,
		@Query("min_price") minPrice: Int = 0,
		@Query("max_price") maxPrice: Int = 99999999,
		@Query("review") review: Int = 0,
	): Call<List<Product>>
}