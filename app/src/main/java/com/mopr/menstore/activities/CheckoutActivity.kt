package com.mopr.menstore.activities

import SharePrefManager
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mopr.menstore.R
import com.mopr.menstore.adapters.CheckOutItemAdapter
import com.mopr.menstore.api.OrderApiService
import com.mopr.menstore.api.ProductApiService
import com.mopr.menstore.api.RetrofitClient
import com.mopr.menstore.api.UserApiService
import com.mopr.menstore.databinding.ActivityCheckoutBinding
import com.mopr.menstore.models.*
import com.mopr.menstore.utils.Formatter
import com.mopr.menstore.utils.OrderApiUtil
import com.mopr.menstore.utils.ProductApiUtil
import com.mopr.menstore.utils.UserApiUtil
import kotlinx.coroutines.launch


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding :  ActivityCheckoutBinding
    private var cartItemsChoosed: List<CartItem> = emptyList()
    private var products : MutableList<Product> = mutableListOf()
    private var cartItemIds: MutableList<Int> = mutableListOf()
    private var images : MutableList<ProductImage> = mutableListOf()
    private var productDetailList : MutableList<ProductDetail> = mutableListOf()
    private var defaultShippingFee : Int = 10000
    private var totalPayment : Int = 0
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var userApiUtil: UserApiUtil
    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var user: User
    private var phone: String = ""
    private var address: String = ""
    private var note: String = ""
    private var name: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.header.tvTitle.text = "Mua hàng"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val jsonSelectedCartItems = intent.getStringExtra("jsonSelectedCartItems")
        val type = object : TypeToken<List<CartItem>>() {}.type
        val gson = Gson()
        user = SharePrefManager.getInstance(this).getUser()
        cartItemsChoosed = gson.fromJson(jsonSelectedCartItems, type)
        for (cartItem in cartItemsChoosed)
            cartItemIds.add(cartItem.id)

        binding.tvEditInfo.setOnClickListener(){ displayEditDialog() }
        binding.btnCartItemBuy.setOnClickListener{
            if (address.isNotEmpty()) confirmOrder()
            else Toast.makeText(this@CheckoutActivity,"Vui lòng điền   nhận hàng!!",Toast.LENGTH_LONG).show()
        }

        fetchData()
    }
    private fun confirmOrder() {
        val confirmDialog = Dialog(this@CheckoutActivity)
        confirmDialog.setContentView(R.layout.confirm_order_dialog)
        val cancelBtn = confirmDialog.findViewById(R.id.btnCancel) as Button
        val OkBtn = confirmDialog.findViewById(R.id.btnOk) as Button
        confirmDialog.setCancelable(false)
        OkBtn.setOnClickListener {
            lifecycleScope.launch {
                orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
                Log.d(TAG, "confirmOrder: name: $name")
                Log.d(TAG, "confirmOrder: name: $phone")
                Log.d(TAG, "confirmOrder: name: $address")
                Log.d(TAG, "confirmOrder: name: $note")
                orderApiUtil.addOrder(user.id.toInt(), name, phone, address, 1, cartItemIds.joinToString(","), note)
                confirmDialog.dismiss()
                displayAddOrderSuccessDialog()
            }

        }
        cancelBtn.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.show()
    }
    private fun displayAddOrderSuccessDialog(){
        val notifyDialog = Dialog(this@CheckoutActivity)
        notifyDialog.setContentView(R.layout.add_order_notify_success_dialog)
        val continueShopping = notifyDialog.findViewById(R.id.btnHome) as Button
        val manageOrder = notifyDialog.findViewById(R.id.btn_manageOrders) as Button
        notifyDialog.setCancelable(false)
        continueShopping.setOnClickListener {
            //tới trang home
            val intent = Intent(this@CheckoutActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
            notifyDialog.dismiss()
        }
        manageOrder.setOnClickListener {
            //tới trang quản lý đơn hàng
            val intent = Intent(this@CheckoutActivity, OrdersActivity::class.java)
            startActivity(intent)
            finish()
            notifyDialog.dismiss()
        }
        notifyDialog.show()
    }
    @SuppressLint("SetTextI18n")
    private fun displayEditDialog()
    {
        val editDialog = Dialog(this)
        editDialog.setContentView(R.layout.edit_info_dialog)
        val cancelBtn = editDialog.findViewById(R.id.btn_cancle) as Button
        val saveBtn = editDialog.findViewById(R.id.btn_save) as Button
        val ed_EditPhone: TextView = editDialog.findViewById(R.id.etPhone)
        val ed_Name: TextView = editDialog.findViewById(R.id.etName)
        val ed_EditAddress: TextView = editDialog.findViewById(R.id.etAddress)
        val ed_EditNote: TextView =  editDialog.findViewById(R.id.etNote)
        editDialog.setTitle("Chỉnh sửa thông tin nhận hàng")
        val lp: WindowManager.LayoutParams = editDialog.window!!.attributes
        lp.width = 1000
        lp.height = 1100
        editDialog.window?.attributes = lp
        editDialog.setCancelable(false)
        if(user.phone.isNotEmpty())
            ed_EditPhone.text = user.phone

        if(user.address.isNotEmpty()){
            ed_EditAddress.text = user.address
        }
        if(user.name.isNotEmpty()){
            ed_Name.text =  user.name
        }
        saveBtn.setOnClickListener {
            lifecycleScope.launch {
                //Hiển thị xác nhận lưu rồi tắt dialog
                name = ed_Name.text.toString()
                phone = ed_EditPhone.text.toString()
                address = ed_EditAddress.text.toString()
                note = ed_EditNote.text.toString()

                if(phone.isNotEmpty() and address.isNotEmpty()) {
                    binding.tvNameCheckout.text = "Tên: $name"
                    binding.tvPhoneCheckout.text = "Số ĐT: $phone"
                    binding.tvAddressCheckout.text = "Địa chỉ: $address"
                    binding.tvNoteCheckout.text = "Ghi chú: $note"
                    editDialog.dismiss()
                }

                else
                    Toast.makeText(this@CheckoutActivity, "Vui lòng điền đủ các thông tin",Toast.LENGTH_LONG).show()
            }
        }
        cancelBtn.setOnClickListener{
            editDialog.dismiss()
        }
        editDialog.show()
    }
    private fun fetchData() {
        lifecycleScope.launch {
            userApiUtil = UserApiUtil(RetrofitClient.getRetrofit().create(UserApiService::class.java))
            user = userApiUtil.getUser(user.id.toInt())!!
            for (item in cartItemsChoosed) {
                productApiUtil= ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
                val productDetail = productApiUtil.getProductDetail(item.productDetailId)
                productDetailList.add(productDetail!!)
                val product = productApiUtil.get(productDetail.productId)
                products.add(product!!)
                val image = productApiUtil.getImages(product.id)
                images.add(image[0])
            }
            bindCheckOutItems(cartItemsChoosed,productDetailList, products,images)
        }
    }
    private  fun totalPayment (cartItemsChoosed: List<CartItem>,productDetailList : MutableList<ProductDetail>): Int {
        for ((i, item) in cartItemsChoosed.withIndex()){
            totalPayment += if (productDetailList[i].onSale) {
                (item.quantity * productDetailList[i].salePrice)
            } else {
                (item.quantity * productDetailList[i].price)
            }
        }
        return totalPayment
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun bindCheckOutItems(cartItemsChoosed: List<CartItem>, productDetailList: MutableList<ProductDetail>, products: MutableList<Product>, images: MutableList<ProductImage>) {
        val checkOutItemsAdapter = CheckOutItemAdapter(this@CheckoutActivity,cartItemsChoosed,productDetailList, products,images)
        binding.rvCheckOutItems.setHasFixedSize(true)
        binding.rvCheckOutItems.adapter = checkOutItemsAdapter
        val totalPayment = totalPayment(cartItemsChoosed,productDetailList)
        checkOutItemsAdapter.notifyDataSetChanged()

        defaultShippingFee = if (totalPayment >= 1000000) 0 else 30000

        binding.tvTemptPayment.text = Formatter.formatVNDAmount(totalPayment.toLong())
        binding.tvShippingFeeCheckOut.text = Formatter.formatVNDAmount(defaultShippingFee.toLong())
        binding.tvTotalPaymentCheckout.text = Formatter.formatVNDAmount((totalPayment + defaultShippingFee).toLong())
        binding.tvTotalPayment.text = Formatter.formatVNDAmount((totalPayment + defaultShippingFee).toLong())

        name = user.name
        phone = user.phone
        address = user.address

        binding.tvNameCheckout.text = "Tên: $name"
        binding.tvPhoneCheckout.text = "Số ĐT: $phone"
        binding.tvAddressCheckout.text = "Địa chỉ: $address"
        binding.tvNoteCheckout.text = "Ghi chú: $note"
    }

    companion object {
        const val TAG = "CheckoutActivity"
    }
}

