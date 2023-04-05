package com.mopr.menstore.api

import com.mopr.menstore.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
	companion object {
		var gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create()
		fun getRetrofit(): Retrofit {
			return Retrofit.Builder()
				.baseUrl(Constants.BASE_URL)
				.addConverterFactory(gsonConverterFactory)
				.build()
		}
	}
}