package com.mopr.menstore.api

import com.mopr.menstore.api.response.AllCategories
import retrofit2.Call
import retrofit2.http.GET

interface ProductApiService {
	@GET("category/get-all/")
	fun getAllCategories(): Call<AllCategories>
}