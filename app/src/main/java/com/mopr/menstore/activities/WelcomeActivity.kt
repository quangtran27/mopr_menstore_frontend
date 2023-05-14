package com.mopr.menstore.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mopr.menstore.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
	private lateinit var binding: ActivityWelcomeBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityWelcomeBinding.inflate(layoutInflater)
		setContentView(binding.root)
		Handler(Looper.getMainLooper()).postDelayed({
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}, 1500)
	}
}