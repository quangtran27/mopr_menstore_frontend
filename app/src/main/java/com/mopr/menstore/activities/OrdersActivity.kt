package com.mopr.menstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.mopr.menstore.adapters.OrdersViewPagerAdapter
import com.mopr.menstore.databinding.ActivityOrdersBinding

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var ordersViewPagerAdapter: OrdersViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        ordersViewPagerAdapter = OrdersViewPagerAdapter(this, 1)
        binding.viewPager.adapter = ordersViewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView
            // Thiết lập tiêu đề cho mỗi tab
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