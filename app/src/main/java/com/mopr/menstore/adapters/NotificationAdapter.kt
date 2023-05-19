package com.mopr.menstore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ItemNotifyBinding
import com.mopr.menstore.models.Notification

class NotificationAdapter(private val context: Context, private val notifications: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {
	private var listener: OnItemClickListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
		val binding = ItemNotifyBinding.inflate(
			LayoutInflater.from(parent.context), parent, false
		)
		return NotificationVH(binding)
	}

	override fun getItemCount() = notifications.size

	override fun onBindViewHolder(holder: NotificationVH, position: Int) {
		holder.bind(notifications[position])
	}

	inner class NotificationVH(val binding: ItemNotifyBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(notification: Notification) {
			binding.tvTitle.text = notification.title
			binding.tvBody.text = notification.body
			binding.tvTime.text = notification.created

			if (!notification.isChecked) {
				binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.background_noti))
			}

			binding.root.setOnClickListener {
				listener?.onItemClick(adapterPosition)
			}
		}
	}

	fun setOnItemClickListener(listener: OnItemClickListener) {
		this.listener = listener
	}

	interface OnItemClickListener {
		fun onItemClick(position: Int)
	}
}