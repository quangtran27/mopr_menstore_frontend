package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mopr.menstore.adapters.ReviewAdapter
import com.mopr.menstore.databinding.ActivityAllReviewBinding
import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import com.mopr.menstore.models.User

class AllReviewActivity : AppCompatActivity() {
	private lateinit var binding: ActivityAllReviewBinding
	private var reviews: List<Review> = emptyList()
	private var users: List<User> = emptyList()
	private var reviewImagesList: List<List<ReviewImage>> = emptyList()
	private lateinit var reviewAdapter: ReviewAdapter

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAllReviewBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.header.tvTitle.text = "Tất cả đánh giá"
		binding.header.ibBack.setOnClickListener {
			onBackPressedDispatcher.onBackPressed()
		}

		getReviewsInfo()
		bindReviewsInfo()

	}

	private fun getReviewsInfo() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			reviews = intent.getParcelableArrayListExtra("reviews", Review::class.java)!!
			users = intent.getParcelableArrayListExtra("users", User::class.java)!!
		} else {
			reviews = intent.getParcelableArrayListExtra("reviews")!!
			users = intent.getParcelableArrayListExtra("users")!!
		}
		val jsonReviewImagesList = intent.getStringExtra("jsonReviewImagesList")
		val type = object : TypeToken<List<List<ReviewImage>>>() {}.type
		val gson = Gson()
		reviewImagesList = gson.fromJson(jsonReviewImagesList, type)
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindReviewsInfo() {
		reviewAdapter =
			ReviewAdapter(this@AllReviewActivity, reviews, users, reviewImagesList, reviews.size)
		binding.rvReviews.adapter = reviewAdapter
		reviewAdapter.notifyDataSetChanged()
	}

	companion object {
		const val TAG = "AllReviewActivity"
	}
}