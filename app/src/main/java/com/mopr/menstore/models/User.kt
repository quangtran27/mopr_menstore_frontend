package com.mopr.menstore.models

import java.util.Date

data class User(
	val name: String,
	val phone: String,
	val password: String,
	val birthday: Date,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
)
