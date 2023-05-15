package com.mopr.menstore.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mopr.menstore.activities.OrdersActivity
import com.mopr.menstore.fragments.orders.*


class OrdersViewPagerAdapter (
    ordersActivity: OrdersActivity,
    private val userId: Int
    ) : FragmentStateAdapter(ordersActivity){
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> ToPayFragment.newInstance(userId)
            1 -> ToShipFragment.newInstance(userId)
            2 -> ToReceiveFragment.newInstance(userId)
            3 -> CompletedFragment.newInstance(userId)
            4 -> CancelledFragment.newInstance(userId)
            else -> ToPayFragment.newInstance(userId)
        }
    }
}