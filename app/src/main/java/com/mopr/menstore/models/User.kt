package com.mopr.menstore.models

import java.util.Date

data class User(
	val id: String,
	val name: String,
	val phone: String,
	val password: String,
	val birthday: String,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
)
