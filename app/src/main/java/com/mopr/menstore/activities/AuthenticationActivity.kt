package com.mopr.menstore.activities

import SharePrefManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Processing click sign up button
        binding.signup.setOnClickListener {
            binding.layoutLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.layoutRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            binding.tvNote.text =
                "Bằng cách Đăng ký tài khoản vào shop, bạn đã đồng ý với Chính sách cookie và Điều khoản bảo mật của chúng tôi!"
            binding.loginLayout.visibility = View.GONE
            binding.signupLayout.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
            binding.signupBtn.visibility = View.VISIBLE

        }
        //Processing click Login button
        binding.login.setOnClickListener {
            binding.layoutRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.layoutLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            binding.tvNote.text =
                "Bằng cách Đăng nhập vào shop, bạn đã đồng ý với Chính sách cookie và Điều khoản bảo mật của chúng tôi!"
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
        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Vui lòng nhập thông tin!"
            binding.password.requestFocus()
        }
        if (TextUtils.isEmpty(phone)) {
            binding.username.error = "Vui lòng nhập thông tin!"
            binding.username.requestFocus()
        } else if (!isPhoneNumberValid(phone)) {
            binding.username.error = "Số điện thoại không hợp lệ!"
            binding.username.requestFocus()
        } else {
            //Initialize a UserApiService object from the Retrofit object
            val userLoginApi = RetrofitClient.getRetrofit().create(UserApiService::class.java)

            userLoginApi.login(phone, password).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Login success, handle response data
                        val userResp = response.body()
                        if (userResp != null) {
                            val user: User = userResp
                            //Save user info
                            SharePrefManager.getInstance(applicationContext).saveUser(user)

                            //Inform success login
                            Toast.makeText(
                                applicationContext,
                                "Đăng nhập thành công!",
                                Toast.LENGTH_SHORT
                            ).show()

                            //Forward to HomePage
                            finish()
                            val intent =
                                Intent(this@AuthenticationActivity, MainActivity::class.java)
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

    private fun signup() {
        //Get info from UI
        val phone = binding.phoneSignup.text.toString()
        val name = binding.nameSignup.text.toString()
        val password = binding.passwordSignup.text.toString()
        val email = binding.emailSignup.text.toString()

        // Validate inputs
        if (TextUtils.isEmpty(phone)) {
            binding.phoneSignup.error = "Vui lòng nhập thông tin!"
            binding.phoneSignup.requestFocus()
            return
        }
        if (TextUtils.isEmpty(name)) {
            binding.nameSignup.error = "Vui lòng nhập thông tin!"
            binding.nameSignup.requestFocus()
            return
        }
        if (TextUtils.isEmpty(email)) {
            binding.emailSignup.error = "Vui lòng nhập thông tin!"
            binding.emailSignup.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordSignup.error = "Vui lòng nhập thông tin!"
            binding.passwordSignup.requestFocus()
            return
        }
        if (!isPhoneNumberValid(phone)) {
            binding.phoneSignup.error = "Số điện thoại không hợp lệ!"
            binding.phoneSignup.requestFocus()
            return
        }
        if (!isPasswordValid(password)) {
            binding.passwordSignup.error = "Mật khẩu không đúng định dạng!"
            binding.passwordSignup.requestFocus()
            return
        }
        if (!isGmailValid(email)) {
            binding.emailSignup.error = "Email không đúng!"
            binding.emailSignup.requestFocus()
            return
        } else {
            //Initialize a UserApiService object from the Retrofit object
            val userSignupApi = RetrofitClient.getRetrofit().create(UserApiService::class.java)
            userSignupApi.signup(phone, name, password, email).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Register successfully
                        if (response.code() == 201) {
                            Toast.makeText(
                                applicationContext,
                                "Đăng ký thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Register error
                        Toast.makeText(
                            applicationContext,
                            "Số điện thoại/email đã được sử dụng!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Error in calling API",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }

    //A range of number must begin with 0 and have 10 characters
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val regex = Regex("^(\\+84|0)\\d{9,10}$")
        return regex.matches(phoneNumber)
    }

    //Password must contain more than 7 characters and have some special characters
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&_+=\\-])[^\\s]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    //Email must obey Gmail format
    private fun isGmailValid(email: String): Boolean {
        val pattern = Regex("^[a-zA-Z0-9._%+-]+@gmail\\.com$")
        return pattern.matches(email)
    }
}