package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserApiUtil(private val userApiService: UserApiService) {
	suspend fun getUser(userId: Int): User? {
		return withContext(Dispatchers.IO) {
			try {
				val response = userApiService.getUser(userId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				}
				if (!response.isSuccessful) {
					throw ApiException("Error getting user by id. Status code: ${response.code()}")
				}
			}
			catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext null
		}
	}

	suspend fun getCart(userId: Int): Cart? {
		return withContext(Dispatchers.IO) {
			try {
				val response = userApiService.getCart(userId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				} else {
					Log.d(TAG, "The user with ID $userId does not exist")
				}
			}
			catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext null
		}
	}

	companion object {
		const val  TAG = "UserApiUtil"
	}
}