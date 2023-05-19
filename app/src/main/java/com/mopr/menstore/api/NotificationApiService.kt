package com.mopr.menstore.api

import com.mopr.menstore.models.Notification
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApiService {
	@FormUrlEncoded
	@PUT("notifications/{notificationId}")
	fun updateNotification(
		@Path(value = "notificationId", encoded = true) notificationId: Int,
		@Field("isChecked") isChecked: String
	): Call<Notification>
}