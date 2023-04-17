package com.mopr.menstore.api

import com.mopr.menstore.api.response.AllCategoriesResponse
import com.mopr.menstore.api.response.AllProductsResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {
	@GET("category/get-all/")
	fun getAllCategories(): Call<AllCategoriesResponse>

	@GET("product/get-top-sales")
	fun getTopSaleProduct(): Call<AllProductsResponse>

	@GET("product/get-latest")
	fun getLatestProducts(): Call<AllProductsResponse>

	@GET("product/search")
	fun search(
		@Query("keyword") keyword: String = "",
		@Query("sort_by") sortBy: String = "price",
		@Query("order") order: String = "asc",
		@Query("category_id") categoryId: Int = 0,
		@Query("min_price") minPrice: Int = 0,
		@Query("max_price") maxPrice: Int = 99999999,
		@Query("review") review: Int = 0,
	): Call<AllProductsResponse>
}