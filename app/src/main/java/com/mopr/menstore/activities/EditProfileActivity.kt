package com.mopr.menstore.activities

import SharePrefManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityEditProfileBinding
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(this)

        setContentView(binding.root)
        binding.header.tvTitle.text = "Chỉnh sửa thông tin cá nhân"
        binding.header.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        loadData()
    }

    private fun saveBirthday(user: User, userApiUtil: UserApiUtil) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val newDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                //Save into database
                lifecycleScope.launch {
                    user.email?.let {
                        val isSuccess: Boolean = userApiUtil.updateUserInfo(
                            user.id, user.name, user.address, newDate,
                            it, user.image.toString()
                        )
                        if (isSuccess) {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Đổi thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            //  Reload the activity
                            finish()
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Thật bại!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun saveName(user: User, userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Sửa tên")

        val input = EditText(this@EditProfileActivity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newName = input.text.toString()
            //Save into database
            lifecycleScope.launch {
                user.email?.let {
                    val isSuccess: Boolean = userApiUtil.updateUserInfo(
                        user.id, newName, user.address, user.birthday,
                        it, user.image.toString()
                    )
                    if (isSuccess) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Đổi thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reload the activity
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Thật bại!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveEmail(user: User, userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Sửa email")

        val input = EditText(this@EditProfileActivity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newEmail = input.text.toString()
            if (isGmailValid(newEmail)) {
                //Save into database
                lifecycleScope.launch {
                    val isSuccess: Boolean = userApiUtil.updateUserInfo(
                        user.id, user.name, user.address, user.birthday,
                        newEmail, user.image.toString()
                    )
                    if (isSuccess) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Đổi thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reload the activity
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Thật bại!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this@EditProfileActivity, "Email không đúng!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveAddress(user: User, userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Sửa địa chỉ")

        val input = EditText(this@EditProfileActivity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newAddress = input.text.toString()
            //Save into database
            lifecycleScope.launch {
                user.email?.let {
                    val isSuccess: Boolean = userApiUtil.updateUserInfo(
                        user.id, user.name, newAddress, user.birthday,
                        it, user.image.toString()
                    )
                    if (isSuccess) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Đổi thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Reload the activity
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Thật bại!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    //Email must obey Gmail format
    private fun isGmailValid(email: String): Boolean {
        val pattern = Regex("^[a-zA-Z0-9._%+-]+@gmail\\.com$")
        return pattern.matches(email)
    }

    private fun loadData() {
        val userSaved: User = sharePrefManager.getUser()
        val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        val userApiUtil = UserApiUtil(userApiService)

        lifecycleScope.launch {
            val user: User? = userApiUtil.getUserInfo(userSaved.id)
            //Display user on UI
            if (user!!.image != null) {
                Glide.with(this@EditProfileActivity).load(Constants.BASE_URL1 + user.image)
                    .into(binding.ivAvatar)
            } else {
                binding.ivAvatar.setBackgroundResource(R.drawable.avatar)
            }
            binding.tvUsername.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserAddress.text = user.address
            binding.tvUserBirthday.text = user.birthday.split("-").reversed().joinToString("-")
            //Handle edit
            binding.ivAvatar.setOnClickListener {
                startActivity(Intent(this@EditProfileActivity, UploadUserImageActivity::class.java))
            }
            binding.tvDate.setOnClickListener {
                saveBirthday(user, userApiUtil)
            }
            binding.tvName.setOnClickListener {
                saveName(user, userApiUtil)
            }
            binding.tvEmail.setOnClickListener {
                saveEmail(user, userApiUtil)
            }
            binding.tvAddress.setOnClickListener {
                saveAddress(user, userApiUtil)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}