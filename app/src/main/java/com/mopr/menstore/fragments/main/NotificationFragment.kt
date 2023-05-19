package com.mopr.menstore.fragments.main

import SharePrefManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.mopr.menstore.activities.OrderDetailsActivity
import com.mopr.menstore.adapters.NotificationAdapter
import com.mopr.menstore.api.NotificationApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentNotificationBinding
import com.mopr.menstore.models.Notification
import com.mopr.menstore.utils.NotificationApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class NotificationFragment : Fragment() {
	private lateinit var binding: FragmentNotificationBinding
	private lateinit var notificationApiUtil: NotificationApiUtil
	private lateinit var userApiUtil: UserApiUtil
	private lateinit var notifications: MutableList<Notification>
	private var userId by Delegates.notNull<Int>()

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentNotificationBinding.inflate(layoutInflater)
		notificationApiUtil = NotificationApiUtil(RetrofitClient.getRetrofit().create(NotificationApiService::class.java))
		userApiUtil = UserApiUtil(RetrofitClient.getRetrofit().create(UserApiService::class.java))
		notifications = mutableListOf()
		userId = -1

		binding.header.tvTitle.text = "Thông báo"
		if (SharePrefManager.getInstance(requireContext()).isLoggedIn()) {
			binding.llEmptyNotifications.visibility = View.VISIBLE
			userId = SharePrefManager.getInstance(requireContext()).getUser().id.toInt()
		} else {
			binding.llGuest.visibility = View.VISIBLE
		}

		fetchData()
	}

	private fun fetchData() {
		lifecycleScope.launch {
			notifications = userApiUtil.getNotifications(userId).toMutableList()
			bindData()
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun bindData() {
		if (notifications.isNotEmpty()) {
			binding.llEmptyNotifications.visibility = View.GONE
			val adapter = NotificationAdapter(requireContext(), notifications)
			binding.rvNotifications.setHasFixedSize(true)
			binding.rvNotifications.adapter = adapter
			adapter.notifyDataSetChanged()

			adapter.setOnItemClickListener(object: NotificationAdapter.OnItemClickListener {
				override fun onItemClick(position: Int) {
					val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
					intent.putExtra("orderId", notifications[position].orderId)
					requireContext().startActivity(intent)

					lifecycleScope.launch {
						notificationApiUtil.updateNotification(notifications[position].id, "True")
					}
				}

			})
		}
	}
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = binding.root
}