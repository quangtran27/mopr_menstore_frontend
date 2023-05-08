package com.mopr.menstore.api

import com.mopr.menstore.models.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApiService {
	@GET("categories/")
	fun getAllCategories(): Call<List<Category>>

	@GET("categories/{categoryId}")
	fun getCategory(@Path(value="categoryId", encoded = true) categoryId: Int): Call<Category>
}