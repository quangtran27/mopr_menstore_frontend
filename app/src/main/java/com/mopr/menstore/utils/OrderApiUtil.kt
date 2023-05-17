package com.mopr.menstore.utils

import android.util.Log
import com.mopr.menstore.api.ApiException
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.models.Order
import com.mopr.menstore.models.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderApiUtil (private val orderApiService: OrderApiService) {
    suspend fun getOrdersByUser(userId: Int): List<Order>{
        return withContext(Dispatchers.IO) {
            try {
                val response = orderApiService.getOrdersByUser(userId).execute()
                if (response.isSuccessful) {
                    return@withContext response.body()!!
                }else {
                    throw ApiException("Error getting orders. Status code: ${response.code()}")
                }
            }catch (e: Exception){
                return@withContext listOf()
            }
        }
    }
    suspend fun getOrderItems(orderId: Int): List<OrderItem>{
        return withContext(Dispatchers.IO){
            try {
                val response = orderApiService.getOrderItems(orderId).execute()
                if (response.isSuccessful) {
                    return@withContext response.body()!!
                }else{
                    throw ApiException("Error getting orderItems. Status code: ${response.code()}")
                }
            }catch (e: Exception){
                return@withContext listOf()
            }
        }
    }
    suspend fun getOrder(orderId: Int): Order?{
        return withContext(Dispatchers.IO){
            try {
                val response = orderApiService.getOrder(orderId).execute()
                if (response.isSuccessful) {
                    return@withContext response.body()!!
                }else{
                    throw ApiException("Error getting orderItems. Status code: ${response.code()}")
                }
            }catch (e: Exception){
                return@withContext null
            }
        }
    }
    suspend fun updateOrder(orderId: Int, status: Int, isPaid: Int, isReviewed: Int) {
        return withContext(Dispatchers.IO){
            try {
                val response = orderApiService.updateOrder(orderId, status, isPaid, isReviewed).execute()
                if(!response.isSuccessful){
                    throw ApiException("Error update order. Status code: ${response.code()}")
                }
            }
            catch (e: Exception){
                Log.d("updateOrderError", e.message.toString())
            }
        }
    }

    suspend fun addOrder(userId: Int, name: String, phone: String, address: String, payment: Int, cart_item_ids: String, note: String) {
        return withContext(Dispatchers.IO){
            try {
                val response = orderApiService.addOrder(userId,name,phone,address,payment,cart_item_ids,note).execute()
                if(!response.isSuccessful){
                    throw ApiException("Error add order. Status code: ${response.code()}")
                }
            }
            catch (e: Exception){
                Log.d("addOrderError", e.message.toString())
            }
        }

    }

}