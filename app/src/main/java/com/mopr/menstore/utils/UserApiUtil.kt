package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.Order
import com.mopr.menstore.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

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

	suspend fun getOrders(userId: String): List<Order>? {
		return withContext(Dispatchers.IO) {
			try {
				val response = userApiService.getOrders(userId).execute()
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

	suspend fun getUserInfo(userId: String): User? {
		return withContext(Dispatchers.IO) {
			try {
				val response = userApiService.getUserInfo(userId).execute()
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

    suspend fun updateUserInfo(userId: String, name: String, address: String, birthday: String, email: String, image: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApiService.updateUserInfo(userId, name, address, birthday, email, image).execute()
                response.isSuccessful
            }
            catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                false
            }
        }
    }

	companion object {
		const val  TAG = "UserApiUtil"
	}
}