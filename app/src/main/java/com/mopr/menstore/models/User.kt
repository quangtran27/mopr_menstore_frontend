package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
	val id: String,
	val name: String,
	val phone: String,
	val password: String,
	val birthday: String,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
	val address: String
) : Parcelable
