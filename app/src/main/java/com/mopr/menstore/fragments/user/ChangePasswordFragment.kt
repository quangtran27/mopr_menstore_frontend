package com.mopr.menstore.fragments.user

import SharePrefManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentChangePasswordBinding
import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userSaved: User = sharePrefManager.getUser()

        binding.sendBtn.setOnClickListener{
            val oldPassword = binding.oldPasswordEdt.text.toString()
            val newPassword = binding.newPasswordEdt.text.toString()
            val confirmPassword = binding.confirmPasswordEdt.text.toString()

            // Validate input
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(),"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!isPasswordValid(newPassword)){
                Toast.makeText(requireContext(),"Mật khẩu mới sai định dạng!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(),"Mật khẩu mới và xác nhận mật khẩu không khớp!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Initialize an ApiService object from the Retrofit object
            val api = RetrofitClient.getRetrofit().create(UserApiService::class.java)
            api.changePassword(userSaved.id, oldPassword, newPassword).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Đổi thành công
                        if (response.code() == 200) {
                            Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                        } else if (response.code() == 404) {
                            Toast.makeText(requireContext(), "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(),"Mật khẩu cũ sai!",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(),"api failed!",Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePasswordFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    //Password must contain more than 7 characters and have some special characters
    private fun isPasswordValid(password: String):Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&_+=\\-])[^\\s]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }
}