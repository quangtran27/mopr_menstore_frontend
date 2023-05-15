package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.CartApiService
import com.mopr.menstore.models.Cart
import com.mopr.menstore.models.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
	suspend fun getAllCartItem(cartId: Int): List<CartItem> {
		return withContext(Dispatchers.IO) {
			try {
				val cartItems = cartApiService.getCartItems(cartId).execute()
				//Log.d("ChauAnh", response.body().toString())
				if (cartItems.isSuccessful) {
					val cartItems = cartItems.body()!!
					return@withContext cartItems
				}
				else {
					throw ApiException("Error getting cart items. Status code: ${cartItems.code()}")
				}
			}catch (e: Exception) {
				Log.d("chauanh", e.message.toString())
			}
			return@withContext listOf()
		}
	}
	suspend fun updateCartItem(cartId: Int, cartItemId: Int, quantity: Int){
		return withContext(Dispatchers.IO){
			try {
				val response = cartApiService.deleteCartItem(cartId, cartItemId, quantity).execute()
				if(!response.isSuccessful){
					throw ApiException("Error update cart. Status code: ${response.code()}")
				}
			}
			catch (e: Exception){
				Log.d("updateCartError", e.message.toString())
			}
		}
	}

}