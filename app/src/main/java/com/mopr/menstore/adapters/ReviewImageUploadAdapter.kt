package com.mopr.menstore.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.databinding.ItemReviewUploadImageBinding
import com.mopr.menstore.utils.Constants

class ReviewImageUploadAdapter(
    private val context: Context,
    private val reviewImages: MutableList<Uri>,
    private val listener: OnItemClickListener?
): RecyclerView.Adapter<ReviewImageUploadAdapter.ReviewImageViewHolder>() {
    inner class ReviewImageViewHolder(private val binding: ItemReviewUploadImageBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Uri, position: Int){
            Glide.with(context).load(image).into(binding.imvReview)
            binding.tvRemoveImage.setOnClickListener {
                // Gọi phương thức onDeleteClick của interface
                listener?.onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        val binding = ItemReviewUploadImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reviewImages.size
    }

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        val image = reviewImages[position]
        holder.bind(image, position)
    }
    interface OnItemClickListener  {
        fun onDeleteClick(position: Int)
    }
}