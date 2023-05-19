package com.mopr.menstore.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mopr.menstore.adapters.OrdersManageViewPagerAdapter
import com.mopr.menstore.databinding.ActivityOrdersManageBinding

class OrdersManageActivity : AppCompatActivity() {
    private lateinit var ordersManageViewPagerAdapter: OrdersManageViewPagerAdapter

    private lateinit var binding: ActivityOrdersManageBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.header.tvTitle.text = "Quản lý đơn hàng"
        binding.header.ibBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        ordersManageViewPagerAdapter = OrdersManageViewPagerAdapter(this@OrdersManageActivity)
        binding.viewPager.adapter = ordersManageViewPagerAdapter

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