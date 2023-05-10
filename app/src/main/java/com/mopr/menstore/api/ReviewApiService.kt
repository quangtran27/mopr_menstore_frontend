package com.mopr.menstore.api

import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

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
    
    @Multipart
    @POST("reviews/")
    fun addReview(
        @Part("user_id") userId: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part("star") star: RequestBody,
        @Part("desc") description: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Call<Review>

}