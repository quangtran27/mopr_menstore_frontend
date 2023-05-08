package com.mopr.menstore.api

import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewApiService {
	@GET("reviews/{reviewId}")
	fun getReview(@Path(value = "reviewId", encoded = true) reviewId: Int): Call<Review>

	@GET("reviews/{reviewId}/images")
	fun getReviewImages(
		@Path(
			value = "reviewId",
			encoded = true
		) reviewId: Int
	): Call<List<ReviewImage>>
}