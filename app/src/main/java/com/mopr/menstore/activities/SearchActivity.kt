package com.mopr.menstore.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.mopr.menstore.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {
	private lateinit var binding: ActivitySearchBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivitySearchBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.header.ibBack.setOnClickListener {
			onBackPressedDispatcher.onBackPressed()
		}

		binding.header.etSearch.requestFocus()
		val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.showSoftInput(binding.header.etSearch, InputMethodManager.SHOW_IMPLICIT)

		binding.header.ibSearch.setOnClickListener {
			val keyword = binding.header.etSearch.text.toString().trim()
			if (keyword.isNotBlank()) {
				val intent = Intent(this, SearchResultActivity::class.java)
				intent.putExtra("keyword", keyword)
				startActivity(intent)
			}
		}
	}

	companion object {
		private const val TAG = "SearchActivity"
	}
}