package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.models.Category
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductApiUtil(private val productApiService: ProductApiService) {
	suspend fun getAllProduct(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getAllProducts().execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting all products. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("getAllProducts", e.message.toString())
			}
			return@withContext listOf()
		}
	}

	suspend fun getProduct(productId: Int): Product? {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getProduct(productId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting product by id. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("get_product_by_id", e.message.toString())
			}
			return@withContext null
		}
	}

	suspend fun getProductDetails(productId: Int): List<ProductDetail> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getProductDetails(productId).execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<ProductDetail>() ?: emptyList<ProductDetail>()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting product by id. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("get_product_by_id", e.message.toString())
			}
			return@withContext emptyList<ProductDetail>()
		}
	}

	suspend fun getProductImages(productId: Int): List<ProductImage> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getProductImages(productId).execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<ProductImage>() ?: emptyList<ProductImage>()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting product by id. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("get_product_by_id", e.message.toString())
			}
			return@withContext emptyList<ProductImage>()
		}
	}

	suspend fun getProductReviews(productId: Int): List<Review> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getProductReviews(productId).execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<Review>() ?: emptyList<Review>()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting product by id. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("get_product_by_id", e.message.toString())
			}
			return@withContext emptyList<Review>()
		}
	}

	suspend fun getTopSaleProducts(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getTopSaleProduct().execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				}
				if (!response.isSuccessful) {
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
				if (response.code() == 200) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting top latest products. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d("getTopSaleProducts", e.message.toString())
			}
			return@withContext listOf()
		}
	}

	suspend fun searchProducts(
		keyword: String = "",
		page: Int = 1,
		sortBy: String = "price",
		order: String = "asc",
		categoryId: Int = 0,
		minPrice: Int = 0,
		maxPrice: Int = 99999999,
		review: Int = 0,
	): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.searchProducts(keyword, page, sortBy, order, categoryId, minPrice, maxPrice, review).execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				} else {
					throw ApiException("Error searching product. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d("search_product", e.message.toString())
				return@withContext emptyList<Product>()
			}
		}
	}
 }
