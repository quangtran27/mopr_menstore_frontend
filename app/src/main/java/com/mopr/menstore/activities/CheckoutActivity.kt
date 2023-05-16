package com.mopr.menstore.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var userId: Int = 1 //Lấy từ sharedPrefercence
    private var totalPayment : Int = 0
    private lateinit var productApiUtil: ProductApiUtil
    private lateinit var userApiUtil: UserApiUtil
    private lateinit var orderApiUtil: OrderApiUtil
    private lateinit var user: User
    private var phone: String = ""
    private var address: String= ""
    private var note: String= ""
    private var name: String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var jsonSelectedCartItems = intent.getStringExtra("jsonSelectedCartItems")
        val type = object : TypeToken<List<CartItem>>() {}.type
        val gson = Gson()
        cartItemsChoosed = gson.fromJson(jsonSelectedCartItems, type)
        for (cartItem in cartItemsChoosed)
            cartItemIds.add(cartItem.id)
        fetchData()
        binding.tvEditInfo.setOnClickListener(){
            displayEditDialog()
        }
        binding.btnCartItemBuy.setOnClickListener{
            lifecycleScope.launch {
                if(address.isNotEmpty()){
                    confirmOrder()
                }
                else
                    Toast.makeText(this@CheckoutActivity,"Vui lòng điền địa chỉ nhận hàng!!",Toast.LENGTH_LONG).show()

            }
        }
    }
    private fun confirmOrder() {
        var confirmDialog = Dialog(this@CheckoutActivity)
        confirmDialog.setContentView(R.layout.confirm_order_dialog)
        var cancelBtn = confirmDialog.findViewById(R.id.btnCancel) as Button
        var OkBtn = confirmDialog.findViewById(R.id.btnOk) as Button
        confirmDialog.setCancelable(false)
        OkBtn.setOnClickListener {
            lifecycleScope.launch {
                orderApiUtil = OrderApiUtil(RetrofitClient.getRetrofit().create(OrderApiService::class.java))
                orderApiUtil.addOrder(user.id, name, phone, address, 1, cartItemIds, note)
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
        var notifyDialog = Dialog(this@CheckoutActivity)
        notifyDialog.setContentView(R.layout.add_order_notify_success_dialog)
        var continueShopping = notifyDialog.findViewById(R.id.btnHome) as Button
        val manageOrder = notifyDialog.findViewById(R.id.btn_manageOrders) as Button
        notifyDialog.setCancelable(false)
        continueShopping.setOnClickListener {
            //tới trang home
            val intent = Intent(this@CheckoutActivity,MainActivity::class.java)
            startActivity(intent)
        }
        manageOrder.setOnClickListener {
            //tới trang quản lý đơn hàng
            val intent = Intent(this@CheckoutActivity, OrdersActivity::class.java)
            startActivity(intent)
        }
        notifyDialog.show()
    }
    private fun displayEditDialog()
    {
        var editDialog = Dialog(this)
        editDialog.setContentView(R.layout.edit_info_dialog)
        var cancelBtn = editDialog.findViewById(R.id.btn_cancle) as Button
        val saveBtn = editDialog.findViewById(R.id.btn_save) as Button
        var ed_EditPhone: TextView = editDialog.findViewById(R.id.etPhone)
        var ed_Name: TextView = editDialog.findViewById(R.id.etName)
        var ed_EditAddress: TextView = editDialog.findViewById(R.id.etAddress)
        var ed_EditNote: TextView =  editDialog.findViewById(R.id.etNote)
        editDialog.setTitle("Chỉnh sửa thông tin nhận hàng")
        val lp: WindowManager.LayoutParams = editDialog.getWindow()!!.getAttributes()
        lp.width = 1000
        lp.height = 1100
        editDialog.getWindow()?.setAttributes(lp)
        editDialog.setCancelable(false)
        if(user.phone.isNotEmpty())
            ed_EditPhone.text = user.phone.toString()

        if(user.address!!.isNotEmpty()){
            ed_EditAddress.text = user.address.toString()
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
                if(phone.isNotEmpty() and address.isNotEmpty()){
                    binding.tvNameCheckout.text = "Tên: " + name
                    binding.tvPhoneCheckout.text = "Số ĐT: " + phone
                    binding.tvAddressCheckout.text = "Địa chỉ: "  + address
                    binding.tvNoteCheckout.text = "Ghi chú: " + note
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
            user = userApiUtil.getUser(userId)!!
            for (item in cartItemsChoosed) {
                productApiUtil= ProductApiUtil(RetrofitClient.getRetrofit().create(ProductApiService::class.java))
                val productDetail = productApiUtil.getProductDetail(item.productDetailId)
                productDetailList.add(productDetail!!)
                val product = productApiUtil.get(productDetail.productId)
                products.add(product!!)
                val image = productApiUtil.getImages(product.id)
                images.add(image[0]!!)
            }
            bindCheckOutItems(cartItemsChoosed,productDetailList, products,images)
            Log.d("chauanh","bind successfully")
        }
    }
    private  fun totalPayment (cartItemsChoosed: List<CartItem>,productDetailList : MutableList<ProductDetail>): Int {
        var i = 0;
        for (item in cartItemsChoosed){
            if(productDetailList[i].onSale){
                totalPayment += (item.quantity * productDetailList[i].salePrice)
            }
            else{
                totalPayment += (item.quantity * productDetailList[i].price)
            }
            i++
        }
        Log.d("ChauAnh",totalPayment.toString())
        return totalPayment
    }
    private fun bindCheckOutItems(cartItemsChoosed: List<CartItem>, productDetailList: MutableList<ProductDetail>, products: MutableList<Product>, images: MutableList<ProductImage>) {
        if (cartItemsChoosed.isNotEmpty()) {
            val checkOutItemsAdapter = CheckOutItemAdapter(this@CheckoutActivity,cartItemsChoosed,productDetailList, products,images)
            binding.rvCheckOutItems.setHasFixedSize(true)
            binding.rvCheckOutItems.adapter = checkOutItemsAdapter
            binding.rvCheckOutItems.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
            var totalPayment = totalPayment(cartItemsChoosed,productDetailList)
            checkOutItemsAdapter.notifyDataSetChanged()
            binding.tvTemptPayment.text = totalPayment.toString() + "đ"
            if (totalPayment >= 1000000){
                defaultShippingFee = 0
            }
            else{
                defaultShippingFee = 30000
            }
            binding.tvShippingFeeCheckOut.text = defaultShippingFee.toString() + "đ"
            binding.tvTotalPaymentCheckout.text = (totalPayment + defaultShippingFee).toString() + "đ"
            binding.tvTotalPayment.text = (totalPayment + defaultShippingFee).toString() + "đ"
            if (user != null) {
                Log.d("ChauAnh",user.phone.toString())
                binding.tvNameCheckout.text = "Tên: " + user.name.toString()
                binding.tvPhoneCheckout.text = "Số ĐT: "+ user.phone
                binding.tvAddressCheckout.text = "Địa chỉ: " + user.address
                binding.tvNoteCheckout.text = "Ghi chú: " + note
            }
        } else {
            Log.d("ChauAnh","Không có món hàng nào trong giỏ hàng")
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMainFragmentContainer, fragment)
            .commit()
    }
}

