package com.mopr.menstore.activities

import SharePrefManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityChangePasswordBinding
import com.mopr.menstore.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(this)
        setContentView(binding.root)

        binding.header.tvTitle.text = "Đổi mật khẩu"
        binding.header.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val userSaved: User = sharePrefManager.getUser()

        binding.sendBtn.setOnClickListener {
            val oldPassword = binding.oldPasswordEdt.text.toString()
            val newPassword = binding.newPasswordEdt.text.toString()
            val confirmPassword = binding.confirmPasswordEdt.text.toString()

            // Validate input
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(newPassword)) {
                Toast.makeText(this, "Mật khẩu mới sai định dạng!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(
                    this,
                    "Mật khẩu mới và xác nhận mật khẩu không khớp!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Initialize an ApiService object from the Retrofit object
            val api = RetrofitClient.getRetrofit().create(UserApiService::class.java)
            api.changePassword(userSaved.id, oldPassword, newPassword).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Đổi thành công
                        if (response.code() == 200) {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Đổi thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (response.code() == 404) {
                            Toast.makeText(
                                this@ChangePasswordActivity,
                                "Tài khoản không tồn tại!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Mật khẩu cũ sai!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, "api failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&_+=\\-])[^\\s]{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMainFragmentContainer, fragment)
            .commit()
    }
}