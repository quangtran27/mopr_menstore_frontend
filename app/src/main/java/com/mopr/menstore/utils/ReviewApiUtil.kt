package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.ReviewApiService
import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ReviewApiUtil(private val reviewApiService: ReviewApiService) {
	suspend fun getReview(reviewId: Int): Review? {
		return withContext(Dispatchers.IO) {
			try {
				val response = reviewApiService.getReview(reviewId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				}
				if (!response.isSuccessful) {
					Log.d(
						TAG,
						"Error getting all review (ID: $reviewId). Status code: ${response.code()}"
					)
				}
			} catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext null
		}
	}

	suspend fun getImages(reviewId: Int): List<ReviewImage> {
		return withContext(Dispatchers.IO) {
			try {
				val response = reviewApiService.getReviewImages(reviewId).execute()
				if (response.code() == 200) {
					return@withContext response.body()?.toList<ReviewImage>()
						?: emptyList<ReviewImage>()
				}
				if (!response.isSuccessful) {
					Log.d(
						TAG,
						"Error getting review images (reviewId: $reviewId. Status code: ${response.code()}"
					)
				}
			} catch (e: Exception) {
				Log.d("get_product_by_id", e.message.toString())
			}
			return@withContext emptyList<ReviewImage>()
		}
	}

	companion object {
		const val TAG = "ReviewApiUtil"
	}

	suspend fun add(
		userId: RequestBody,
		productId: RequestBody,
		star: RequestBody,
		description: RequestBody,
		images: List<MultipartBody.Part>
	): Review? {
		return withContext(Dispatchers.IO) {
			try {
				val response =
					reviewApiService.add(userId, productId, star, description, images)
						.execute()

				if (response.isSuccessful) {
					return@withContext response.body()!!
				} else {
					throw ApiException("Error adding review. Status code: ${response.code()}")
				}
			} catch (e: Exception) {
				return@withContext null

			}
		}
	}

}