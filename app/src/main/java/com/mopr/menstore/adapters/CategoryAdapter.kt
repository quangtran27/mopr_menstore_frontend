package com.mopr.menstore.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.activities.SearchActivity
import com.mopr.menstore.databinding.CategoryItemBinding
import com.mopr.menstore.models.Category
import com.mopr.menstore.utils.Constants

class CategoryAdapter(
	private val context: Context,
	private val categories: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

	inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(category: Category) {
			binding.tvCategoryName.text = category.name
			Glide.with(context).load(Constants.BASE_URL + category.image).into(binding.ivCategoryImage)
			binding.root.setOnClickListener {
				val intent = Intent(itemView.context, SearchActivity::class.java)
				intent.putExtra("categoryId", category.id.toString())
				intent.putExtra("categoryName", category.name)
				itemView.context.startActivity(intent)
			}
		}
	}

	override fun getItemCount(): Int = categories.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
		val binding = CategoryItemBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return CategoryViewHolder(binding)
	}

	override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
		val category = categories[position]
		holder.bind(category)
	}
}