package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.databinding.ItemReviewBinding
import com.mopr.menstore.models.Review
import com.mopr.menstore.models.ReviewImage
import com.mopr.menstore.models.User

class ReviewAdapter(
	val context: Context,
	private val reviews: List<Review>,
	private val users: List<User>,
	private val imagesList: List<List<ReviewImage>>,
	private val limit: Int,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
		val binding = ItemReviewBinding.inflate(
			LayoutInflater.from(parent.context), parent, false
		)
		return ReviewViewHolder(binding)
	}

	override fun getItemCount(): Int = if (limit > reviews.size) reviews.size else limit

	override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
		holder.bind(reviews[position], users[position], imagesList[position])
	}

	inner class ReviewViewHolder(val binding: ItemReviewBinding) :
		RecyclerView.ViewHolder(binding.root) {
		@SuppressLint("NotifyDataSetChanged")
		fun bind(review: Review, user: User, images: List<ReviewImage>) {
			binding.tvUserName.text = user.name
			binding.tvReviewBody.text = review.desc
			binding.rbRate.rating = review.star.toFloat()

			val reviewImageAdapter = ReviewImageAdapter(context, images)
			binding.rvReviewImages.adapter = reviewImageAdapter
			reviewImageAdapter.notifyDataSetChanged()

			binding.tvReviewBody.viewTreeObserver.addOnGlobalLayoutListener(object :
				ViewTreeObserver.OnGlobalLayoutListener {
				override fun onGlobalLayout() {
					val lineCount = binding.tvReviewBody.lineCount
					if (lineCount > 2) {
						binding.tvReviewBody.maxLines = 2
						binding.tvReviewBody.ellipsize = TextUtils.TruncateAt.END

						binding.tvSeeMore.setOnClickListener {
							binding.tvSeeMore.visibility = View.GONE
							binding.tvReviewBody.maxLines = Int.MAX_VALUE
						}
					} else {
						binding.tvSeeMore.visibility = View.GONE
					}
					binding.tvReviewBody.viewTreeObserver.removeOnGlobalLayoutListener(this)
				}
			})
		}
	}

	companion object {
		const val TAG = "ReviewAdapter"
	}
}