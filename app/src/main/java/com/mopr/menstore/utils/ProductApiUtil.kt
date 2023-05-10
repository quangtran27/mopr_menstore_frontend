package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.models.ListResponse
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductApiUtil(private val productApiService: ProductApiService) {
	suspend fun getAll(options: Map<String, String>): ListResponse<Product>? {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getAll(options).execute()
				if (response.isSuccessful) {
					return@withContext response.body()
				} else {
					Log.d(TAG, "getAllProduct (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getAllProduct (Exception): ${e.message}")
			}
			return@withContext null
		}
	}

	suspend fun get(productId: Int): Product? {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.get(productId).execute()
				if (response.isSuccessful) {
					return@withContext response.body()
				} else {
					Log.d(TAG, "getProduct (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getProduct (Exception): ${e.message}")
			}
			return@withContext null
		}
	}

	suspend fun getDetail(productDetailId: Int): ProductDetail? {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getDetail(productDetailId).execute()
				if (response.isSuccessful) {
					return@withContext response.body()
				} else {
					Log.d(TAG, "getProductDetails (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getProductDetails (Exception): ${e.message}")
			}
			return@withContext null
		}
	}

	suspend fun getDetails(productId: Int): List<ProductDetail> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getDetails(productId).execute()
				if (response.isSuccessful) {
					return@withContext response.body()?.toList<ProductDetail>()
						?: emptyList<ProductDetail>()
				} else {
					Log.d(TAG, "getProductDetails (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getProductDetails (Exception): ${e.message}")
			}
			return@withContext emptyList<ProductDetail>()
		}
	}

	suspend fun getImages(productId: Int): List<ProductImage> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getImages(productId).execute()
				if (response.isSuccessful) {
					return@withContext response.body()?.toList<ProductImage>()
						?: emptyList<ProductImage>()
				} else {
					Log.d(TAG, "getProductImages (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getProductImages (Exception): ${e.message}")
			}
			return@withContext emptyList<ProductImage>()
		}
	}

	suspend fun getReviews(productId: Int): List<Review> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getReviews(productId).execute()
				if (response.isSuccessful) {
					return@withContext response.body()?.toList<Review>() ?: emptyList<Review>()
				} else {
					Log.d(TAG, "getProductReviews (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getProductReviews (Exception): ${e.message}")
			}
			return@withContext emptyList<Review>()
		}
	}

	suspend fun getTopSale(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getTopSale().execute()
				if (response.isSuccessful) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				} else {
					Log.d(TAG, "getTopSaleProducts (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getTopSaleProducts (Exception): ${e.message}")
			}
			return@withContext listOf()
		}
	}

	suspend fun getLatest(): List<Product> {
		return withContext(Dispatchers.IO) {
			try {
				val response = productApiService.getLatest().execute()
				if (response.isSuccessful) {
					return@withContext response.body()?.toList<Product>() ?: emptyList<Product>()
				} else {
					Log.d(TAG, "getLatestProducts (Error): status code ${response.code()}")
				}
			} catch (e: Exception) {
				Log.d(TAG, "getLatestProducts (Exception): ${e.message}")
			}
			return@withContext listOf()
		}
	}

	companion object {
		const val TAG = "ProductApiUtil"
	}
}
