package com.mopr.menstore.activities

import SharePrefManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mopr.menstore.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
	private lateinit var binding: ActivityWelcomeBinding
	private lateinit var sharePrefManager: SharePrefManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityWelcomeBinding.inflate(layoutInflater)
		sharePrefManager = SharePrefManager.getInstance(this)
		setContentView(binding.root)

		Handler(Looper.getMainLooper()).postDelayed({
			if (sharePrefManager.isLoggedIn()){
				startActivity(Intent(this, MainActivity::class.java))
				finish()
			}
			else{
				startActivity(Intent(this, AuthenticationActivity::class.java))
				finish()
		}}, 1500)
	}

}