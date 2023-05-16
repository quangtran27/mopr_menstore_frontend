package com.mopr.menstore.activities

import SharePrefManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityChangePasswordBinding
import com.mopr.menstore.databinding.ActivityMeDetailBinding
import com.mopr.menstore.databinding.FragmentMeDetailBinding
import com.mopr.menstore.fragments.user.MeFragment
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch

class MeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMeDetailBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeDetailBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(this)
        setContentView(binding.root)

        binding.header.tvTitle.text = "Thông tin cá nhân"
        binding.header.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
       loadData()
    }
    private fun loadData(){
        // Display user info through share-preferences saved
        val userSaved: User = sharePrefManager.getUser()
        val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        val userApiUtil = UserApiUtil(userApiService)

        lifecycleScope.launch {
            val user: User? = userApiUtil.getUserInfo(userSaved.id)
            if (user != null) {
                if (user.image != null) {
                    Glide.with(this@MeDetailActivity).load(Constants.BASE_URL1 + user.image)
                        .into(binding.ivAvatar)
                } else {
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
    }
    override fun onResume() {
        super.onResume()
       loadData()
    }
}