package com.mopr.menstore.models

data class Cart(
	val id: Int,
	val cartItem: List<CartItem>,
)
