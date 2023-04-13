package com.mopr.menstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mopr.menstore.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
	private lateinit var binding: ActivityProductDetailBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityProductDetailBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}
}