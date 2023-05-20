package com.mopr.menstore.models

data class Notification(
	val id: Int,
	val userId: Int,
	val orderId: Int,
	val title: String,
	val body: String,
	val isChecked: Boolean,
	val created: String,
)
