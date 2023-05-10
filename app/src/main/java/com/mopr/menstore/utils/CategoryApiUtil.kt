package com.mopr.menstore.utils

import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.CategoryApiService
import com.mopr.menstore.models.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryApiUtil(private val categoryApiService: CategoryApiService) {
	suspend fun getAllCategories(): List<Category> {
		return withContext(Dispatchers.IO) {
			try {
				val response = categoryApiService.getAllCategories().execute()
				if (response.code() == 200) {
					return@withContext response.body() ?: emptyList<Category>()
				} else {
					throw ApiException("Error getting categories. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				return@withContext emptyList<Category>()
			}
		}
	}

	suspend fun getCategory(categoryId: Int): Category? {
		return withContext(Dispatchers.IO) {
			try {
				val response = categoryApiService.getCategory(categoryId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				} else {
					throw ApiException("Error getting categories. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				return@withContext null
			}
		}

	}
}