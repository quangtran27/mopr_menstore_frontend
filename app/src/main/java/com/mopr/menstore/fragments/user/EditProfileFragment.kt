package com.mopr.menstore.fragments.user

import SharePrefManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentEditProfileBinding
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch
import java.util.*

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var sharePrefManager: SharePrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(requireContext())
        binding.header.tvTitle.text = "Chỉnh sửa thông tin cá nhân"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userSaved: User = sharePrefManager.getUser()
        val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        val userApiUtil = UserApiUtil(userApiService)

        lifecycleScope.launch {
           val user: User? = userApiUtil.getUserInfo(userSaved.id)

            //Display user
            if (user!!.image != null) {
                Glide.with(requireContext()).load(Constants.BASE_URL1 + user.image).into(binding.ivAvatar)
            }
            binding.tvUsername.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserAddress.text = user.address
            binding.tvUserBirthday.text = user.birthday.split("-").reversed().joinToString("-")

            //Handle edit
            binding.ivAvatar.setOnClickListener {
                navigateTo(UploadImageFragment())
            }
            binding.tvDate.setOnClickListener {
                saveBirthday(user,userApiUtil)
            }
            binding.tvName.setOnClickListener {
                saveName(user,userApiUtil)
            }
            binding.tvEmail.setOnClickListener {
                saveEmail(user,userApiUtil)
            }
            binding.tvAddress.setOnClickListener {
                saveAddress(user,userApiUtil)
            }
        }
        return binding.root
    }
    private fun saveBirthday(user:User,userApiUtil: UserApiUtil) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val newDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            //Save into database
            lifecycleScope.launch {
                user.email?.let {
                    val isSuccess:Boolean = userApiUtil.updateUserInfo(user.id,user.name,user.address,newDate,
                        it,user.image.toString())
                    if(isSuccess){
                        Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Thật bại!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }, year, month, day)
        datePickerDialog.show()
    }
    private fun saveName(user:User,userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sửa tên")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newName = input.text.toString()
            //Save into database
            lifecycleScope.launch {
                user.email?.let {
                    val isSuccess:Boolean = userApiUtil.updateUserInfo(user.id,newName,user.address,user.birthday,
                        it,user.image.toString())
                    if(isSuccess){
                        Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(requireView()).navigate(EditProfileFragment())
                    }else{
                        Toast.makeText(requireContext(), "Thật bại!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    private fun saveEmail(user:User,userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sửa email")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newEmail = input.text.toString()
           if(isGmailValid(newEmail)){
               //Save into database
               lifecycleScope.launch {
                   val isSuccess:Boolean = userApiUtil.updateUserInfo(user.id,user.name,user.address,user.birthday,
                       newEmail,user.image.toString())
                   if(isSuccess){
                       Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                   }else{
                       Toast.makeText(requireContext(), "Thật bại!", Toast.LENGTH_SHORT).show()
                   }
               }
           }else{
               Toast.makeText(requireContext(), "Email không đúng!", Toast.LENGTH_SHORT).show()
           }
        }
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
    private fun saveAddress(user:User,userApiUtil: UserApiUtil) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sửa tên")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Lưu") { _, _ ->
            val newAddress = input.text.toString()
            //Save into database
            lifecycleScope.launch {
                user.email?.let {
                    val isSuccess:Boolean = userApiUtil.updateUserInfo(user.id,user.name,newAddress,user.birthday,
                        it,user.image.toString())
                    if(isSuccess){
                        Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Thật bại!", Toast.LENGTH_SHORT).show()
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

    //Navigate
    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.flMainFragmentContainer, fragment)
            .commit()
    }
}