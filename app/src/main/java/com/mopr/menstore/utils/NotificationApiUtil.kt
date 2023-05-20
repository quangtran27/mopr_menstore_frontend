package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.NotificationApiService
import com.mopr.menstore.models.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class NotificationApiUtil(private val notificationApiService: NotificationApiService) {
	suspend fun updateNotification(notificationId: Int, isChecked: String): Notification? {
		return withContext(Dispatchers.IO) {
			try {
				val response = notificationApiService.updateNotification(notificationId, isChecked).execute()
				if (response.isSuccessful) {
					return@withContext response.body()
				} else {
					Log.d(TAG, "updateNotification (Error): status cdoe ${response.code()}")
				}

			} catch (e: Exception) {
				Log.d(TAG, "updateNotification (Exception): ${e.message}")
			}
			return@withContext null
		}
	}

	companion object {
		const val TAG = "NotificationApiUtil"
	}
}