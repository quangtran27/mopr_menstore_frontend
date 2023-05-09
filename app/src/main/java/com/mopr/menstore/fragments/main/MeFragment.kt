package com.mopr.menstore.fragments.main

import SharePrefManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import com.mopr.menstore.R
import com.mopr.menstore.databinding.FragmentMeBinding

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
			val detailInfoFragment = MeDetailFragment()
			parentFragmentManager.beginTransaction()
				.replace(R.id.flMainFragmentContainer, detailInfoFragment)
				.addToBackStack(null)
				.commit()
		}

		//processing click order detail
		binding.tvOrderMore.setOnClickListener{
			val detailOrderFragment = MeDetailFragment()
			parentFragmentManager.beginTransaction()
				.replace(R.id.flMainFragmentContainer, detailOrderFragment)
				.addToBackStack(null)
				.commit()
		}

		//processing click change password
		binding.tvChangePassword.setOnClickListener{
			val changPassFragment = ChangePasswordFragment()
			parentFragmentManager.beginTransaction()
				.replace(R.id.flMainFragmentContainer, changPassFragment)
				.addToBackStack(null)
				.commit()
		}

		//processing click edit profile
		binding.tvEditProfile.setOnClickListener{
			val editProfileFragment = EditProfileFragment()
			parentFragmentManager.beginTransaction()
				.replace(R.id.flMainFragmentContainer, editProfileFragment)
				.addToBackStack(null)
				.commit()
		}

		//processing click logout
		binding.tvLogout.setOnClickListener {
			sharePrefManager.clearUser()
		}

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

}