package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class CartApiUtil(private val cartApiService: CartApiService) {
	suspend fun getCart(cartId: Int): Cart? {
		return withContext(Dispatchers.IO) {
			try {
				val response = cartApiService.getCart(cartId).execute()
				if (response.code() == 200) {
					return@withContext response.body()
				} else if (response.code() == 404) {
					Log.d(TAG, "The cart has ID $cartId does not exist")
				}
			} catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext null
		}
	}

	suspend fun getCartItems(cartId: Int): List<CartItem> {
		return withContext(Dispatchers.IO) {
			try {
				val response = cartApiService.getCartItems(cartId).execute()
				if (response.code() == 200) {
					return@withContext response.body() ?: emptyList<CartItem>()
				} else if (response.code() == 404) {
					Log.d(TAG, "The cart has ID $cartId does not exist")
				}
			} catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext emptyList<CartItem>()
		}
	}

	suspend fun addToCart(cartId: Int, productDetailId: Int, quantity: Int): CartItem? {
		return withContext(Dispatchers.IO) {
			try {
				val response = cartApiService.addToCart(cartId, productDetailId, quantity).execute()
				if (response.isSuccessful) {
					return@withContext response.body()
				}
			} catch (e: Exception) {
				Log.d(TAG, e.message.toString())
			}
			return@withContext null
		}
	}
	companion object {
		const val TAG = "CartApiUtil"
	}
}