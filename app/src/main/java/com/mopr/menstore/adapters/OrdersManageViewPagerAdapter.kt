package com.mopr.menstore.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mopr.menstore.activities.OrdersManageActivity
import com.mopr.menstore.fragments.orders_manage.*

class OrdersManageViewPagerAdapter (
    ordersManageActivity: OrdersManageActivity
    ): FragmentStateAdapter(ordersManageActivity) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {

        return when (position){
            0 -> ToPayManageFragment()
            1 -> ToShipManageFragment()
            2 -> ToReceiveManageFragment()
            3 -> CompletedManageFragment()
            4 -> CancelledManageFragment()
            else -> ToPayManageFragment()
        }
    }

}