package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
	val id: String,
	val name: String,
	val phone: String,
	val password: String,
	val birthday: String,
	val address: String,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
) : Parcelable
