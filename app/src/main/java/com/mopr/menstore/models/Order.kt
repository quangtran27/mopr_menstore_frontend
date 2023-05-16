package com.mopr.menstore.models

data class Order(
	val id: Int,
	val userId: Int,
	val created: String, // yyyy-mm-dd
	val updated: String, // yyyy-mm-dd
	val status: Int, // 1: Chờ xác nhận, 2: Chờ lấy hàng, 3: Đang giao hàng, 4: Đã giao, 5: Đã hủy
	val name: String,
	var phone: String,
	var address: String,
	var payment: Int, // 1: COD, 2: Chuyển khoản ngân hàng, 3: Khác
	val isPaid: Boolean,
	val isReviewed: Boolean,
	var note: String,
	val total: Int,
//	val orderItems: List<OrderItem>,
)
