package com.mopr.menstore.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityStaffLoginBinding
import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mopr.menstore.databinding.ActivityStaffLoginBinding

class StaffLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        //Get info from UI
        val phone = binding.username.text.toString()
        val password = binding.password.text.toString()

        // Validate inputs
        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Vui lòng nhập thông tin!"
            binding.password.requestFocus()
        }
        if (TextUtils.isEmpty(phone)) {
            binding.username.error = "Vui lòng nhập thông tin!"
            binding.username.requestFocus()
        }
        //Initialize a UserApiService object from the Retrofit object
        val userLoginApi = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        userLoginApi.staffLogin(phone, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Login success, handle response data
                    val userResp = response.body()
                    if (userResp != null) {
                        //Inform success login
                        Toast.makeText(
                            applicationContext,
                            "Đăng nhập thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        //Forward to HomePage
                        finish()
                        val intent =
                            Intent(this@StaffLoginActivity, StaffMainActivity::class.java)
                        startActivity(intent)
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
}