package com.mopr.menstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mopr.menstore.databinding.ActivityProductBinding

class ProductActivity : AppCompatActivity() {
	private lateinit var binding: ActivityProductBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityProductBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}
}