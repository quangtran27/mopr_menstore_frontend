package com.mopr.menstore.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mopr.menstore.R
import com.mopr.menstore.databinding.ActivityMainBinding

class StaffMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bnMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.orderManagerPage -> {
                    startActivity(Intent(this@StaffMainActivity, OrdersManageActivity::class.java))
                }
                R.id.productsPage -> {
                }
            }
            true
        }

        binding.bnMenu.selectedItemId = R.id.orderManagerPage
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flMainFragmentContainer, fragment)
            .commit()
    }
}