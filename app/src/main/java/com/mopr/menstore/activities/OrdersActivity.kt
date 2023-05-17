package com.mopr.menstore.activities

import SharePrefManager
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mopr.menstore.adapters.OrdersViewPagerAdapter
import com.mopr.menstore.databinding.ActivityOrdersBinding
import com.mopr.menstore.models.User

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var ordersViewPagerAdapter: OrdersViewPagerAdapter
    private lateinit var user: User

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Đơn mua"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        user = SharePrefManager.getInstance(this).getUser()

        ordersViewPagerAdapter = OrdersViewPagerAdapter(this, user.id.toInt())
        binding.viewPager.adapter = ordersViewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView
            when (position){
                0 -> tab.text ="Chờ xác nhận"
                1 -> tab.text ="Chờ lấy hàng"
                2 -> tab.text ="Đang giao hàng"
                3 -> tab.text ="Đã giao"
                4 -> tab.text ="Đã hủy"
            }
        }.attach()
    }
}