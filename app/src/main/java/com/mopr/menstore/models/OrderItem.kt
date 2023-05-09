package com.mopr.menstore.models

data class OrderItem(
	val id: Int,
	val orderId: Int,
	val productDetailId: Int,
	val onSale: Boolean,
	val price: Int,
	val salePrice: Int,
	val quantity: Int,
)
