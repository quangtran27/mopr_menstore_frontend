package com.mopr.menstore.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mopr.menstore.databinding.ActivityWelcomeBinding
import android.os.Looper

class WelcomeActivity : AppCompatActivity() {
	private lateinit var binding: ActivityWelcomeBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityWelcomeBinding.inflate(layoutInflater)
		setContentView(binding.root)

		Handler(Looper.getMainLooper()).postDelayed({
			startActivity(Intent(this, HomeActivity::class.java))
			finish()
		}, 1500)
	}
}