//package com.mopr.exercise
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.mopr.menstore.a.LoginActivity
//import com.mopr.menstore.models.User
//import java.time.LocalDate
//
//class SharedPrefManager(context: Context?) {
//	init {
//		Companion.context = context
//	}
//
//	fun userLogin(user: User) {
//		val sharedPreferences = context!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//		val editor = sharedPreferences.edit()
//		editor.putInt(KEY_ID, user.id)
//		editor.putString(KEY_USERNAME, user.name)
//		editor.putString(KEY_PHONE, user.phone)
//		editor.putString(KEY_BIRTHDAY, user.birthday.toString())
//		editor.putString(KEY_GENDER, user.gender)
//		editor.putString(KEY_EMAIL, user.email)
//		editor.putString(KEY_IMAGES, user.images)
//		editor.apply()
//	}
//
//	val isLoggedIn: Boolean
//		get() {
//			val sharedPreferences =
//				context!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//			return sharedPreferences.getString(KEY_USERNAME, null) != null
//		}
//	val user: User
//		@RequiresApi(Build.VERSION_CODES.O)
//		get() {
//			val sharedPreferences =
//				context!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//			return User(
//				sharedPreferences.getInt(KEY_ID, -1),
//				sharedPreferences.getString(KEY_USERNAME, null).toString(),
//				sharedPreferences.getString(KEY_PHONE, null).toString(),
//				LocalDate.parse(sharedPreferences.getString(KEY_BIRTHDAY, null)),
//				sharedPreferences.getString(KEY_GENDER, null).toString(),
//				sharedPreferences.getString(KEY_EMAIL, null).toString(),
//				sharedPreferences.getString(KEY_IMAGES, null).toString(),
//			)
//		}
//
//	fun logout() {
//		val sharedPreferences =
//			context!!.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
//		val editor = sharedPreferences.edit()
//		editor.clear()
//		editor.apply()
//		context!!.startActivity(
//			Intent(
//				context,
//				LoginActivity::class.java
//			)
//		)
//	}
//
//	@SuppressLint("StaticFieldLeak")
//	companion object {
//		private const val SHARED_PREF_NAME = "volleyauthentication"
//		private const val KEY_ID = "keyid"
//		private const val KEY_USERNAME = "keyusername"
//		private const val KEY_PHONE = "keyphone"
//		private const val KEY_BIRTHDAY = "keybirthday"
//		private const val KEY_EMAIL = "keyemail"
//		private const val KEY_GENDER = "keygender"
//		private const val KEY_IMAGES = "keyimages"
//
//		@SuppressLint("StaticFieldLeak")
//		private var instance: SharedPrefManager? = null
//		@SuppressLint("StaticFieldLeak")
//		private var context: Context? = null
//		@Synchronized
//		fun getInstance(context: Context): SharedPrefManager? {
//			if (instance == null) {
//				instance = SharedPrefManager(context)
//			}
//			return instance
//		}
//	}
//}

import android.content.Context
import android.content.SharedPreferences
import com.mopr.menstore.models.User
import java.text.SimpleDateFormat
import java.util.*

class SharePrefManager private constructor(private val context: Context) {

	companion object {
		private const val SHARED_PREF_NAME = "menstore_shared_pref"
		private const val KEY_NAME = "name"
		private const val KEY_PHONE = "phone"
		private const val KEY_PASSWORD = "password"
		private const val KEY_BIRTHDAY = "birthday"
		private const val KEY_GENDER = "gender"
		private const val KEY_EMAIL = "email"
		private const val KEY_IMAGES = "image"

		@Volatile
		private var INSTANCE: SharePrefManager? = null

		fun getInstance(context: Context) =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: SharePrefManager(context).also { INSTANCE = it }
			}
	}

	private val sharedPreferences: SharedPreferences by lazy {
		context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
	}

	fun saveUser(user: User) {
		val editor = sharedPreferences.edit()
		editor.putString(KEY_NAME, user.name)
		editor.putString(KEY_PHONE, user.phone)
		editor.putString(KEY_PASSWORD,user.password)
		editor.putString(KEY_BIRTHDAY, user.birthday.toString())
		editor.putString(KEY_GENDER, user.gender)
		editor.putString(KEY_EMAIL, user.email)
		editor.putString(KEY_IMAGES, user.image)
		editor.apply()
	}

	fun getUser(): User? {
		val name = sharedPreferences.getString(KEY_NAME, null)
		val phone = sharedPreferences.getString(KEY_PHONE, null)
		val password =sharedPreferences.getString(KEY_PASSWORD, null)
		val birthday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(sharedPreferences.getString(
			KEY_BIRTHDAY, null))
		val gender = sharedPreferences.getString(KEY_GENDER, null)
		val email = sharedPreferences.getString(KEY_EMAIL, null)
		val images = sharedPreferences.getString(KEY_IMAGES, null)

		return User( name!!, phone!!, password!!, birthday!!, gender!!, email!!, images!!)
	}

	fun clearUser() {
		val editor = sharedPreferences.edit()
		editor.clear()
		editor.apply()
	}

}