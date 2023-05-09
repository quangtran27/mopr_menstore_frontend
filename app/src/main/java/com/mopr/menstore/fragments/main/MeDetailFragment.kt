package com.mopr.menstore.fragments.main

import SharePrefManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.mopr.menstore.R
import com.mopr.menstore.activities.MainActivity
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentMeBinding
import com.mopr.menstore.databinding.FragmentMeDetailBinding
import com.mopr.menstore.models.User
import com.squareup.picasso.Picasso
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Display user info through share-preferences saved
        val userSaved: User = sharePrefManager.getUser()

        //Initialize a UserApiService object from the Retrofit object
        val userLoginApi = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        userLoginApi.getUserInfo(userSaved.id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    //Get User from id
                    val userResp = response.body()
                    if(userResp!= null){
                        val user:User = userResp
                        // Load user avatar using Glide
                        Glide.with(requireContext()).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTI4FtESFKahWDRVuWJcim_MsGi2BRkr4Irrg&usqp=CAU").into(binding.ivAvatar)
                        binding.tvId.text = user.id
                        binding.tvName.text = user.name
                        binding.tvEmail.text = user.email
                        binding.tvPhone.text = user.phone
                        binding.tvDate.text = user.birthday
                        binding.tvGender.text = user.gender
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(),"API failed", Toast.LENGTH_SHORT).show()
            }

        })
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MeDetailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}