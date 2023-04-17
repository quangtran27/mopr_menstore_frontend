package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Field

class ProductApiUtil(private val productApiService: ProductApiService) {
	suspend fun getTopSaleProducts(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getTopSaleProduct().execute()
				if (response.isSuccessful) {
					val allProducts = response.body()!!
					if (allProducts.success) {
						return@withContext allProducts.data
					} else {
						throw ApiException("Error getting top sale products. Status code: ${response.code()}")
					}
				} else {
					throw ApiException("Error getting top sale products. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("getTopSaleProducts", e.message.toString())
			}
			return@withContext listOf()
		}
	}

	suspend fun getLatestProducts(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getLatestProducts().execute()

				if (response.isSuccessful) {
					val allProducts = response.body()!!
					if (allProducts.success) {
						return@withContext allProducts.data
					} else {
						throw ApiException("Error getting latest products. Status code: ${response.code()}")
					}
				} else {
					throw ApiException("Error getting latest products. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("getTopSaleProducts", e.message.toString())
			}
			return@withContext listOf()
		}
	}

	suspend fun getAllCategories(): List<Category> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getAllCategories().execute()
				if (response.isSuccessful) {
					val allCategories = response.body()!!
					if (allCategories.success) {
						return@withContext allCategories.data
					} else {
						throw ApiException("Error getting categories. Status code: ${response.code()}")
					}
				} else {
					throw ApiException("Error getting categories. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				return@withContext listOf()
			}
		}
	}

	suspend fun search(
		keyword: String = "",
		sortBy: String = "price",
		order: String = "asc",
		categoryId: Int = 0,
		minPrice: Int = 0,
		maxPrice: Int = 99999999,
		review: Int = 0,
	): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.search(keyword, sortBy, order, categoryId, minPrice, maxPrice, review).execute()
				if (response.isSuccessful) {
					val allProductsResponse = response.body()!!
					if (allProductsResponse.success) {
						return@withContext allProductsResponse.data
					} else {
						throw ApiException("Error searching product. Status code: ${response.code()}")
					}
				} else {
					throw ApiException("Error searching product. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d("search_product", e.message.toString())
				return@withContext listOf()
			}
		}
	}
 }
