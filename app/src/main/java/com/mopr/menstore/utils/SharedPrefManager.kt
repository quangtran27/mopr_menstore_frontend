import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.mopr.menstore.activities.AuthenticationActivity
import com.mopr.menstore.models.User
import java.util.*

class SharePrefManager private constructor(private val context: Context) {

	companion object {
		private const val SHARED_PREF_NAME = "menstore_shared_pref"
		private const val KEY_ID = "id"
		private const val KEY_PHONE = "phone"
		private const val KEY_PASSWORD = "password"

		@SuppressLint("StaticFieldLeak")
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
		editor.putString(KEY_ID, user.id)
		editor.putString(KEY_PHONE, user.phone)
		editor.putString(KEY_PASSWORD,user.password)
		editor.apply()
	}

	fun getUser(): User {
		val id = sharedPreferences.getString(KEY_ID, "-1")
		val phone = sharedPreferences.getString(KEY_PHONE, "")
		val password = sharedPreferences.getString(KEY_PASSWORD, "")
		return User(id!!, "", phone!!, password!!, "", "", "", "","")
	}

	//Logout
	fun clearUser() {
		val editor = sharedPreferences.edit()
		editor.clear()
		editor.apply()
		val intent = Intent(context, AuthenticationActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		context.startActivity(intent)
	}

	//Check login
	fun isLoggedIn(): Boolean {
		val id = sharedPreferences.getString(KEY_ID, null)
		return id != null
	}
}