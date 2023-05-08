package com.mopr.menstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ItemCategoryBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.utils.Constants

class CategoryAdapter(
	private val context: Context,
	private val categories: List<Category>,
	private val activeId: Int = 0
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
	private var listener: OnItemClickListener? = null
	inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
		RecyclerView.ViewHolder(binding.root) {

		@SuppressLint("ResourceAsColor")
		fun bind(category: Category) {
			binding.tvCategoryName.text = category.name
			if (category.image != null) {
				Glide.with(context).load(Constants.BASE_URL + category.image).into(binding.ivCategoryImage)
			} else {
				Glide.with(context).load(R.drawable.logo).into(binding.ivCategoryImage)
				binding.ivCategoryImage.setBackgroundColor(R.color.primary)
				binding.ivCategoryImage.scaleType = ImageView.ScaleType.FIT_CENTER
			}

			if (category.id == activeId) {
				binding.main.setBackgroundColor(ContextCompat.getColor(context, R.color.background_gray))
				binding.root.setOnClickListener(null)
				binding.root.scrollTo(adapterPosition, 0)
			}
		}
	}

	override fun getItemCount(): Int = categories.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
		val binding = ItemCategoryBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return CategoryViewHolder(binding)
	}

	override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
		val category = categories[position]
		holder.bind(category)
		holder.binding.root.setOnClickListener {
			listener?.onItemClick(category.id)
		}
	}

	companion object {
		const val TAG = "CategoryAdapter"
	}

	interface OnItemClickListener {
		fun onItemClick(categoryId: Int)
	}

	fun setOnItemClickListener(listener: OnItemClickListener) {
		this.listener = listener
	}
}