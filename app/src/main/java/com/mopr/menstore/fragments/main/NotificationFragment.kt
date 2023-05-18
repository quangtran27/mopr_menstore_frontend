package com.mopr.menstore.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mopr.menstore.R
import com.mopr.menstore.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

	private lateinit var binding: FragmentNotificationBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = FragmentNotificationBinding.inflate(layoutInflater)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return binding.root
	}
}