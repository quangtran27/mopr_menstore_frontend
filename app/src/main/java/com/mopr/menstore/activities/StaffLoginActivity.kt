package com.mopr.menstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mopr.menstore.databinding.ActivityStaffLoginBinding

class StaffLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}