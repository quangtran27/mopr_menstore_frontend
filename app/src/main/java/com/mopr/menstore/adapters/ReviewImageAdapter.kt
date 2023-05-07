package com.mopr.menstore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.ItemReviewImageBinding
import com.mopr.menstore.models.ReviewImage
import com.mopr.menstore.utils.Constants

class ReviewImageAdapter(
	val context: Context, private val images: List<ReviewImage>
) : RecyclerView.Adapter<ReviewImageAdapter.ReviewImageViewHolder>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
		val binding = ItemReviewImageBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return ReviewImageViewHolder(binding)
	}

	override fun getItemCount(): Int = images.size

	override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
		holder.bind(images[position])
	}

	inner class ReviewImageViewHolder(val binding: ItemReviewImageBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(image: ReviewImage) {
			Glide.with(binding.root).load(Constants.BASE_URL + image.image).into(binding.ivReviewImage)
		}
	}
}