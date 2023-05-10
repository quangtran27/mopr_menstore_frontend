package com.mopr.menstore.fragments.user

import SharePrefManager
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.RealPathUtil
import com.mopr.menstore.activities.ReviewActivity
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.FragmentUploadImageBinding
import com.mopr.menstore.models.User
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class UploadImageFragment : Fragment() {
    private lateinit var binding: FragmentUploadImageBinding
    private lateinit var sharePrefManager: SharePrefManager
    private lateinit var imageUri:Uri

    companion object {
        private val storge_permissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)

        val storge_permissions_33 = arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
        fun permissions(): Array<out String> {
            val p: Array<out String>
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                p= storge_permissions_33
            }else{
                p= storge_permissions
            }
            return p
        }
        //        val TAG = UploadImageFragment.javaClass.getName()!!
        const val MY_REQUEST_CODE : Int = 100
    }
    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "onActivityResult")
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent = result.data!!
            val uri : Uri = data.data!!
            imageUri = uri
            try{
                Glide.with(requireContext()).load(imageUri).into(binding.ivAvatar)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentUploadImageBinding.inflate(layoutInflater)
        sharePrefManager = SharePrefManager.getInstance(requireContext())
        val userApiService = RetrofitClient.getRetrofit().create(UserApiService::class.java)
        binding.header.tvTitle.text = "Đổi ảnh đại diện"
        val userApiUtil = UserApiUtil(userApiService)

        lifecycleScope.launch {
           val user:User? = userApiUtil.getUserInfo(sharePrefManager.getUser().id)
            if (user!!.image != null) {
                Glide.with(requireContext()).load(Constants.BASE_URL1 + user.image).into(binding.ivAvatar)
            }
        }

        binding.ivChoose.setOnClickListener {
            checkPermission()
        }
        binding.btnSave.setOnClickListener{
            uploadImage(userApiUtil,userApiService)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    private fun checkPermission(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery()
        }else{
            requestPermissions(permissions(), MY_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }
        }
    }
    private fun openGallery(){
        val intent : Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select picture"))
    }

    private fun uploadImage(userApiUtil: UserApiUtil, userApiService:UserApiService) {
        val userSaved = sharePrefManager.getUser()

        lifecycleScope.launch {
            val user: User? = userApiUtil.getUserInfo(userSaved.id)

            val imageFile = File(RealPathUtil.getRealPath(requireContext(), imageUri)!!)
            val requestImageFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
            val imageUser =
                MultipartBody.Part.createFormData("image", imageFile.name, requestImageFile)

            val userNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), user!!.name)
            val userEmailBody =
                user.email?.let { RequestBody.create(MediaType.parse("multipart/form-data"), it) }
            val userBirthdayBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), user.birthday)
            val userAddressBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), user.address)

            userApiService.uploadUserImage(
                user!!.id,
                userNameBody,
                userAddressBody,
                userBirthdayBody,
                userEmailBody!!,
                imageUser
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        Toast.makeText(requireContext(), "Đổi thành công!", Toast.LENGTH_SHORT).show()
                    }else{

                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // handle failure
                    Toast.makeText(requireContext(), "API Failed!", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}