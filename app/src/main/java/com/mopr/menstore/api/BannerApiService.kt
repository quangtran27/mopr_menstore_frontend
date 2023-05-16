package com.mopr.menstore.api

import com.mopr.menstore.models.Banner
import retrofit2.Call
import retrofit2.http.GET

interface BannerApiService {
	@GET("banners/")
	fun getAll(): Call<List<Banner>>
}