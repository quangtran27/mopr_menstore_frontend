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
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mopr.menstore.R
import com.mopr.menstore.RealPathUtil
import com.mopr.menstore.adapters.WriteReviewAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.ReviewApiService
import com.mopr.menstore.databinding.ActivityReviewBinding
import com.mopr.menstore.models.Product
import com.mopr.menstore.models.ProductDetail
import com.mopr.menstore.models.ProductImage
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.ReviewApiUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class ReviewActivity : AppCompatActivity(){
    private var posision : Int = 0
    private var userId: Int = -1
    private var orderId: Int = -1

    private var listImageUris : MutableList<MutableList<Uri>> = mutableListOf()
    private var products: MutableList<Product> = mutableListOf()
    private var productDetails: MutableList<ProductDetail> = mutableListOf()
    private var productFirstImages: MutableList<ProductImage> = mutableListOf()
    private var ratings: MutableList<Int> = mutableListOf()
    private var descs: MutableList<String> = mutableListOf()

    private lateinit var binding: ActivityReviewBinding

    private lateinit var writeReviewAdapter: WriteReviewAdapter

    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var reviewApiUtil: ReviewApiUtil

    private lateinit var progressBar: ProgressBar
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Đánh giá sản phẩm"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        progressBar = ProgressBar(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            visibility = View.GONE
        }
        binding.root.addView(progressBar)

        binding.btnSendReview.setOnClickListener {
            writeReviewAdapter.getAllEditText(object: WriteReviewAdapter.EditTextListener{
                override fun onGetAllEditText(editTextValues: MutableList<String>) {
                    descs = editTextValues
                }
            })
            writeReviewAdapter.getAllRatingBar(object: WriteReviewAdapter.RatingBarListener{
                override fun onRatingChanged(ratingValues: MutableList<Int>) {
                    ratings = ratingValues
                }
            })
            showDialogSendReview(userId, orderId)
        }

        userId = intent.getIntExtra("userId", 1)
        orderId = intent.getIntExtra("orderId", 1)

        orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
        productApiUtil = ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
        reviewApiUtil = ReviewApiUtil(RetrofitClient.getRetrofit().create(ReviewApiService::class.java))

        fetchData(orderId)
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            openGallery()
        }else{
            requestPermissions(permissions(), MY_REQUEST_CODE)
        }
    }
    private fun openGallery(){
        val intent = Intent()
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
    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(orderId: Int) {
        lifecycleScope.launch {
            try {
                val orderItems = orderApiUtil.getOrderItems(orderId)
                for (item in orderItems) {
                    val productDetail = productApiUtil.getDetail(item.productDetailId)
                    val product = productApiUtil.get(productDetail!!.productId)
                    val productImage = productApiUtil.getImages(product!!.id)
                    products.add(product)
                    productDetails.add(productDetail)
                    productFirstImages.add(productImage[0])
                }
                listImageUris = MutableList(products.size) { mutableListOf() }
                loadDataReview(products, productDetails, productFirstImages)
            } catch (e: Exception) {
                Log.d(TAG, "fetchData: ${e.message}")
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadDataReview(products: List<Product>, productDetails: List<ProductDetail>, productFirstImages: List<ProductImage>){
        writeReviewAdapter = WriteReviewAdapter(this@ReviewActivity, products, productDetails, productFirstImages, listImageUris)
        writeReviewAdapter.setOnItemClickListener(object : WriteReviewAdapter.OnItemClickListener{
            override fun onUploadImage(position: Int) {
                posision = position
                checkPermission()
                writeReviewAdapter.notifyDataSetChanged()
            }
        })
        binding.rcReviews.setHasFixedSize(true)
        binding.rcReviews.adapter = writeReviewAdapter
        writeReviewAdapter.notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun sendReview (userId: Int, products: MutableList<Product>, stars: List<Int>, descriptions: List<String>, orderId: Int) {
        val userIdBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), userId.toString())
        val productsIdBody: MutableList<RequestBody> = mutableListOf()
        val starsBody: MutableList<RequestBody> = mutableListOf()
        val descriptionsBody: MutableList<RequestBody> = mutableListOf()

        for (item in products) {
            productsIdBody.add(RequestBody.create(MediaType.parse("multipart/form-data"), item.id.toString()))
        }
        for (item in stars) {
            starsBody.add(RequestBody.create(MediaType.parse("multipart/form-data"), item.toString()))
        }
        for (item in descriptions) {
            descriptionsBody.add(RequestBody.create(MediaType.parse("multipart/form-data"), item))
        }

        val imageReviews = MutableList(products.size) { mutableListOf<MultipartBody.Part>() }
        for (i in 0 until  listImageUris.size) {
            for (j in 0 until  listImageUris[i].size) {
                if (listImageUris[i].size > 0) {
                    val imagePath: String = RealPathUtil.getRealPath(this@ReviewActivity, listImageUris[i][j])!!
                    val file = File(imagePath)
                    val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    imageReviews[i].add(MultipartBody.Part.createFormData("images", file.name, requestFile))
                }
            }
        }

        for (i in 0 until  productsIdBody.size) {
            reviewApiUtil.add(userIdBody, productsIdBody[i], starsBody[i], descriptionsBody[i], imageReviews[i])
        }

        orderApiUtil.updateOrder(orderId, 4, 1, 1)
        Toast.makeText(this, "Gửi đánh giá thành công!", Toast.LENGTH_LONG).show()
    }
    private fun showDialogSendReview(userId: Int, orderId: Int){
        val confirmDialog = Dialog(this)
        confirmDialog.setContentView(R.layout.confirm_review_dialog)
        confirmDialog.setTitle("Xác nhận đánh giá")
        confirmDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        confirmDialog.setCancelable(false)
        val okButton: Button = confirmDialog.findViewById(R.id.btnOk)
        val cancelButton: Button  = confirmDialog.findViewById(R.id.btnCancel)

        okButton.setOnClickListener {
            lifecycleScope.launch {
                confirmDialog.dismiss()
                progressBar.visibility = View.VISIBLE
                sendReview(userId, products, ratings, descs, orderId)
                progressBar.visibility = View.GONE
                onBackPressedDispatcher.onBackPressed()
            }
        }
        cancelButton.setOnClickListener { confirmDialog.dismiss() }
        confirmDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "onActivityResult")
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent = result.data!!
            val uri :Uri= data.data!!
            try {
                if (listImageUris[posision].size < 5) {
                    listImageUris[posision].add(uri)
                    writeReviewAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Bạn chỉ có thể chọn tối đa 5 hình ảnh", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        private val storage_permissions = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)
        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)

        val storage_permissions_33 = arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
        fun permissions(): Array<out String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) storage_permissions_33 else storage_permissions
        const val TAG = "ReviewActivity"
        const val MY_REQUEST_CODE = 100
    }
}