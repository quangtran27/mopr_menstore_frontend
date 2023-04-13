package com.mopr.menstore.models

data class Order(
	val id: Int,
	val user: Int,
	val created: String, // yyyy-mm-dd
	val updated: String, // yyyy-mm-dd
	val status: Int, // 1: Chờ xác nhận, 2: Chờ lấy hàng, 3: Đang giao hàng, 4: Đã giao, 5: Đã hủy
	val name: String,
	val phone: String,
	val address: String,
	val payment: Int, // 1: COD, 2: Chuyển khoản ngân hàng, 3: Khác
	val idPaid: Boolean,
	val note: String,
	val total: Int,
	val orderItems: List<OrderItem>,
)
