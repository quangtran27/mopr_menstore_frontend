package com.mopr.menstore.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class User(
	val id: Int,
	val name: String,
	val phone: String,
	val password: String,
	val birthday: Date,
	val gender: String,
	val email: String?,
	val image: String?, // Constants.BASE_URL + <object>.image
	val address: String?
) : Parcelable
