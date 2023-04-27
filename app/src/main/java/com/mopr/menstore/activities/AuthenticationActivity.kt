package com.mopr.menstore.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityAuthenticationBinding
import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Processing click sign up button
        binding.signup.setOnClickListener {
            binding.login.background = null
            binding.signup.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.loginLayout.visibility = View.GONE
            binding.signupLayout.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
            binding.signupBtn.visibility = View.VISIBLE

        }
        //Processing click Login button
        binding.login.setOnClickListener {
            binding.signup.background =null
            binding.login.background = resources.getDrawable(R.drawable.switch_trcks,null)
            binding.signupLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
            binding.signupBtn.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        }

        //Processing Login
        binding.loginBtn.setOnClickListener {
            login()
        }

        //Processing Sign up
        binding.signupBtn.setOnClickListener {
            signup()
        }
    }

    private fun login() {
        //Get info from UI
        val phone = binding.username.text.toString()
        val password = binding.password.text.toString()

        // Validate inputs
        if (TextUtils.isEmpty(phone)) {
            binding.username.error = "Vui lòng nhập thông tin!"
            binding.username.requestFocus()
        }
        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Vui lòng nhập thông tin!"
            binding.password.requestFocus()
        }

        //Initialize a UserApiService object from the Retrofit object
        val userLoginApi = RetrofitClient.getRetrofit().create(UserApiService::class.java)

        userLoginApi.login(phone,password).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Login success, handle response data
                    val userResp = response.body()
                    if(userResp != null){
                        val user: User = userResp
                        //Save user info
                        SharePrefManager.getInstance(applicationContext).saveUser(user)

                        //Inform success login
                        Toast.makeText(
                            applicationContext,
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        user.birthday?.let { Log.d("DATE", it.toString()) }
                    }
                } else {
                    // Login failed, inform failed login
                    Toast.makeText(
                        applicationContext,
                        "Số điện thoại hoặc mật khẩu không đúng!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error in calling Api",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun signup(){

    }
}