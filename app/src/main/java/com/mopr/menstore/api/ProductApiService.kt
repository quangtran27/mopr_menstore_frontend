package com.mopr.menstore.api

import com.mopr.menstore.api.response.AllCategoriesResponse
import com.mopr.menstore.api.response.AllProductsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProductApiService {
	@GET("category/get-all/")
	fun getAllCategories(): Call<AllCategoriesResponse>

	@GET("product/get-top-sales")
	fun getTopSaleProduct(): Call<AllProductsResponse>

	@GET("product/get-latest")
	fun getLatestProducts(): Call<AllProductsResponse>
}