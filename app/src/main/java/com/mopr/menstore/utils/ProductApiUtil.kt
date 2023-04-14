package com.mopr.menstore.utils

import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductApiUtil(private val productApiService: ProductApiService) {

	suspend fun getTopSaleProducts(): List<Product> {
		return withContext(Dispatchers.IO) {
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
	}

	suspend fun getLatestProducts(): List<Product> {
		return withContext(Dispatchers.IO) {
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
	}

	suspend fun getAllCategories(): List<Category> {
		return withContext(Dispatchers.IO) {
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
		}
	}
}
