package com.mopr.menstore.fragments.user

import SharePrefManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mopr.menstore.activities.ChangePasswordActivity
import com.mopr.menstore.activities.EditProfileActivity
import com.mopr.menstore.activities.MeDetailActivity
import com.mopr.menstore.activities.OrdersActivity
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentMeBinding
import com.mopr.menstore.models.Order
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch


class MeFragment : Fragment() {
	private lateinit var binding: FragmentMeBinding
	private lateinit var sharePrefManager: SharePrefManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentMeBinding.inflate(layoutInflater)
		sharePrefManager = SharePrefManager.getInstance(requireContext())
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentMeBinding.inflate(inflater, container, false)

		//processing click me detail information
		binding.tvInfoMore.setOnClickListener {
			startActivity(Intent(requireContext(), MeDetailActivity::class.java))
		}
		binding.tvOrderMore.setOnClickListener{
			startActivity(Intent(requireContext(), OrdersActivity::class.java))
		}
		binding.tvChangePassword.setOnClickListener{
			startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
		}
		binding.tvEditProfile.setOnClickListener{
			startActivity(Intent(requireContext(), EditProfileActivity::class.java))
		}
		binding.tvLogout.setOnClickListener {
			sharePrefManager.clearUser()
		}
		loadData()

		return binding.root
	}

		companion object {
		@JvmStatic
		fun newInstance(param1: String, param2: String) =
			MeFragment().apply {
				arguments = Bundle().apply {
				}
			}
	}
	private fun loadData(){
		val userSaved: User = sharePrefManager.getUser()
		val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
		val userApiUtil = UserApiUtil(userApiService)
		//Display user info
		lifecycleScope.launch {
			val user: User? = userApiUtil.getUserInfo(userSaved.id)
			val orders:List<Order>? = userApiUtil.getOrders(userSaved.id)

			if(user!=null){
				if (user.image != null) {
					Glide.with(requireContext()).load(Constants.BASE_URL1 + user.image).into(binding.ivAvatar)
				}
				binding.tvUserName.text = user.name
				var confirming = 0
				var confirmed= 0
				var delivering = 0
				var delivered = 0
				var canceled = 0

				if (orders != null) {
					for (order in orders) {
						when (order.status) {
							1 -> confirming++
							2 -> confirmed++
							3 -> delivering++
							4 -> delivered++
							5 -> canceled++
						}
					}
					binding.numConfirmed.text = confirming.toString()
					binding.numConfirming.text = confirmed.toString()
					binding.numDelivering.text = delivering.toString()
					binding.numDelivered.text = delivered.toString()
					binding.numCanceled.text = canceled.toString()
				}
			}
		}
	}
	override fun onResume() {
		super.onResume()
		loadData()
	}
}