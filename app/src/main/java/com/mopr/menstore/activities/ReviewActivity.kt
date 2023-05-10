package com.mopr.menstore.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mopr.menstore.R
import com.mopr.menstore.RealPathUtil
import com.mopr.menstore.adapters.ReviewImageUploadAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.ReviewApiService
import com.mopr.menstore.databinding.ActivityReviewBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.Constants
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.ReviewApiUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class ReviewActivity : AppCompatActivity(), ReviewImageUploadAdapter.OnItemClickListener {
    private lateinit var binding: ActivityReviewBinding
    private val imageUris :  MutableList<Uri> = mutableListOf()
    private val products: MutableList<Int> = mutableListOf()
    companion object{
        private val storge_permissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)

        val storge_permissions_33=arrayOf(
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
        val TAG = ReviewActivity.javaClass.getName()!!
        const val MY_REQUEST_CODE : Int = 100
    }
    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "onActivityResult")
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent = result.data!!
            val uri :Uri= data.data!!
            try {
                if (imageUris.size < 5) {
                    imageUris.add(uri)
                    displayImages(imageUris)
                } else {
                    Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 5 hình ảnh", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val userId = intent.getIntExtra("userId", 1)
        val orderId = intent.getIntExtra("orderId", 1)
        fetchData(orderId)
        binding.rantingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val selectedStars = rating.toInt()
            when (selectedStars) {
                1 ->{
                    binding.tvReview.text= "Tệ"
                    binding.tvReview.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                }
                2 ->{
                    binding.tvReview.text= "Không hài lòng"
                    binding.tvReview.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                }
                3 ->{
                    binding.tvReview.text= "Bình thường"
                    binding.tvReview.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                }
                4 ->{
                    binding.tvReview.text= "Hài lòng"
                    binding.tvReview.setTextColor(ContextCompat.getColor(this, R.color.star))
                }
                5 ->{
                    binding.tvReview.text= "Tuyệt vời"
                    binding.tvReview.setTextColor(ContextCompat.getColor(this, R.color.star))
                }
            }
        }
        binding.llAddImage.setOnClickListener{
            checkPermission()
        }
        binding.llAddImageBonus.setOnClickListener {
            checkPermission()
        }
        binding.btnSendReview.setOnClickListener {
            showDialogSendReview(userId, orderId)
        }
    }

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery()
        }else{
            requestPermissions(permissions(), MY_REQUEST_CODE)
        }
    }
    private fun openGallery(){
        val intent : Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select picture"))
    }

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
    private fun displayImages(imagesUri: MutableList<Uri>){
        Log.e("test111",imageUris.size.toString())
        if (imagesUri.size == 0) {
            binding.llImageReview.visibility = View.GONE
            binding.llAddImage.visibility = View.VISIBLE
        }
        if (imagesUri.size > 0 && imagesUri.size < 5 ) {
            binding.llAddImage.visibility = View.GONE
            binding.llAddImageBonus.visibility = View.VISIBLE
            binding.llImageReview.visibility = View.VISIBLE
        }
        if (imagesUri.size == 5) {
            binding.llAddImageBonus.visibility = View.GONE
        }
        if (imagesUri.size > 0) {
            val imageAdapter = ReviewImageUploadAdapter(this@ReviewActivity, imageUris, this@ReviewActivity)
            val layoutManager: RecyclerView.LayoutManager= GridLayoutManager(applicationContext, imagesUri.size)
            binding.rcImageReview.setHasFixedSize(true)
            binding.rcImageReview.layoutManager= layoutManager
            binding.rcImageReview.adapter = imageAdapter
            imageAdapter.notifyDataSetChanged()
        }
    }
    override fun onDeleteClick(position: Int) {
        imageUris.removeAt(position)
        displayImages(imageUris)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(orderId: Int) {
        val orderApiService = RetrofitClient.getRetrofit().create(OrderApiService::class.java)
        val orderApiUtil = OrderApiUtil(orderApiService)
        val productApiService = RetrofitClient.getRetrofit().create(ProductApiService::class.java)
        val productApiUtil = ProductApiUtil(productApiService)
        lifecycleScope.launch {
            try {
                val orderItems = orderApiUtil.getOrderItems(orderId)
                val firstProductDetail = productApiUtil.getDetail(orderItems[0].productDetailId)
                val firstProduct = productApiUtil.get(firstProductDetail!!.productId)
                val firstProductImages = productApiUtil.getImages(firstProduct!!.id)
                for (item in orderItems) {
                    val productDetail = productApiUtil.getDetail(item.productDetailId)
                    products.add(productDetail!!.productId)
                }
                loadDataReview(firstProduct, firstProductDetail, firstProductImages)
            } catch (e: Exception) {
                Log.d("reviewfetchDataError", e.message.toString())
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadDataReview(firstProduct: Product, firstProductDetail: ProductDetail, firstProductImages: List<ProductImage>){
        Glide.with(this@ReviewActivity).load(Constants.BASE_URL1 + firstProductImages[0].image).into(binding.ivImagePro)
        binding.tvNameProduct.text = firstProduct.name
        binding.tvClassifyProduct.text = firstProductDetail.size + ", " + firstProductDetail.color
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun sendReview (userId: Int, productIds: MutableList<Int>, star: Int, desc: String, images: MutableList<Uri>, orderId: Int) {
        val userReviewId :RequestBody= RequestBody.create(MediaType.parse("multipart/form-data"), userId.toString())
        val productReviewIds :MutableList<RequestBody> = mutableListOf()
        for (item in productIds) {
            val productReviewId : RequestBody= RequestBody.create(MediaType.parse("multipart/form-data"), item.toString())
            productReviewIds.add(productReviewId!!)
        }
        val starReview :RequestBody= RequestBody.create(MediaType.parse("multipart/form-data"), star.toString())
        val description :RequestBody= RequestBody.create(MediaType.parse("multipart/form-data"), desc)
        val imageReviews: MutableList<MultipartBody.Part> = mutableListOf()
        for (item in images) {
            var IMAGE_PATH : String= RealPathUtil.getRealPath(this@ReviewActivity, item)!!
            Log.e("ffff", IMAGE_PATH)
            val  file : File = File(IMAGE_PATH)
            val requestFile : RequestBody= RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val imageReview = MultipartBody.Part.createFormData("images", file.name, requestFile)
            imageReviews.add(imageReview)
        }
        val orderApiService = RetrofitClient.getRetrofit().create(OrderApiService::class.java)
        val orderApiUtil = OrderApiUtil(orderApiService)
        val reviewApiService = RetrofitClient.getRetrofit().create(ReviewApiService::class.java)
        val reviewApiUtil = ReviewApiUtil(reviewApiService)
        lifecycleScope.launch {
            try {
                for (item in productReviewIds) {
                    val review = reviewApiUtil.addReview(userReviewId, item, starReview, description, imageReviews)
                }
                orderApiUtil.updateOrder(orderId, 4, 1, 1)
            }catch (e: Exception) {
                Log.d("sendReviewDataError", e.message.toString())
            }
        }
    }
    private fun showDialogSendReview(userId: Int, orderId: Int){
        val confirmDialog = Dialog(this)
        confirmDialog.setContentView(R.layout.confirm_review_dialog)
        confirmDialog.setTitle("Xác nhận đánh giá")
        confirmDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        confirmDialog.setCancelable(false)
        val  okButton: Button = confirmDialog.findViewById(R.id.btnOk);
        val cancelButton: Button  = confirmDialog.findViewById(R.id.btnCancel);
        okButton.setOnClickListener(View.OnClickListener {
            sendReview(userId, products, binding.rantingBar.numStars, binding.edtDescription.text.toString(), imageUris, orderId)
            startActivity(Intent(applicationContext, OrdersActivity::class.java))
            confirmDialog.dismiss()
        })
        cancelButton.setOnClickListener(View.OnClickListener { confirmDialog.dismiss() })
        confirmDialog.show()
    }
}