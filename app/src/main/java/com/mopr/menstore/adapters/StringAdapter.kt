package com.mopr.menstore.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ItemStringBinding

class StringAdapter(
	val context: Context,
	private val items: List<String>,
	private val availableItems: List<String>,
	private val activeItem: String
) :
	RecyclerView.Adapter<StringAdapter.ViewHolder>() {
	private var listener: OnItemClickListener? = null
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemStringBinding.inflate(
			LayoutInflater.from(parent.context), parent, false
		)
		return ViewHolder(binding)
	}

	override fun getItemCount(): Int {
		return items.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val content = items[position]

		if (items[position] in availableItems) {
			holder.bind(content)
			holder.binding.root.setOnClickListener {
				listener?.onItemClick(position)
			}
		} else {
			holder.bind(content)
		}
	}

	inner class ViewHolder(val binding: ItemStringBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(item: String) {
			binding.tvContent.text = item
			if (item in availableItems) {
				binding.root.setOnClickListener {
					listener
				}
			} else {
				binding.tvContent.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
			}

			if (item == activeItem) {
				binding.root.setOnClickListener(null)
				binding.root.setBackgroundResource(R.drawable.primary_border_background)
				binding.tvContent.setTypeface(null, Typeface.BOLD)
			}
		}
	}

	interface OnItemClickListener {
		fun onItemClick(position: Int)
	}

	fun setOnItemClickListener(listener: OnItemClickListener) {
		this.listener = listener
	}

	companion object {
		const val TAG = "StringAdapter"
	}
}