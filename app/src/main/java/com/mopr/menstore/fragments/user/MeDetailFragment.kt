package com.mopr.menstore.fragments.user

import SharePrefManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentMeDetailBinding
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeDetailFragment : Fragment() {
    private lateinit var binding: FragmentMeDetailBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMeDetailBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(requireContext())
        binding.header.tvTitle.text = "Thông tin cá nhân"
        binding.header.ibBack.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.flMainFragmentContainer, MeFragment())
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Display user info through share-preferences saved
        val userSaved: User = sharePrefManager.getUser()
        val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        val userApiUtil = UserApiUtil(userApiService)

        lifecycleScope.launch {
            val user: User? = userApiUtil.getUserInfo(userSaved.id)
            if (user!=null){
                if (user!!.image != null) {
                    Glide.with(requireContext()).load(Constants.BASE_URL1 + user.image).into(binding.ivAvatar)
                }else{
                    binding.ivAvatar.setBackgroundResource(R.drawable.avatar)
                }
                binding.tvName.text = user.name
                binding.tvEmail.text = user.email
                binding.tvPhone.text = user.phone
                binding.tvDate.text = user.birthday.split("-").reversed().joinToString("-")
                binding.tvGender.text = user.gender
                binding.tvAddress.text = user.address
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}