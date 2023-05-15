package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.BannerApiService
import com.mopr.menstore.models.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BannerApiUtil(private val bannerApiService: BannerApiService) {
	suspend fun getAll(): List<Banner> {
		return withContext(Dispatchers.IO) {
			try {
				val response = bannerApiService.getAll().execute()
				if (response.isSuccessful) {
					return@withContext response.body() ?: emptyList<Banner>()
				} else {
					Log.d(ProductApiUtil.TAG, "getAll (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(ProductApiUtil.TAG, "getAllProduct (Exception): ${e.message}")
			}
			return@withContext emptyList<Banner>()
		}
	}
}