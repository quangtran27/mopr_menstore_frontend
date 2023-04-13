package com.mopr.menstore.utils

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.mopr.menstore.R

class MenuUtils {
	companion object {
		fun activeHome(ivMenuHome: ImageView, tvMenuHome: TextView) {
			ivMenuHome.setImageResource(R.drawable.pic_home_active)
			tvMenuHome.setTypeface(null, Typeface.BOLD)
		}
		fun activeProducts(ivMenuProducts: ImageView, tvMenuProducts: TextView) {
			ivMenuProducts.setImageResource(R.drawable.pic_shirt_active)
			tvMenuProducts.setTypeface(null, Typeface.BOLD)
		}
		fun activeNoti(ivMenuNoti: ImageView, tvMenuNoti: TextView) {
			ivMenuNoti.setImageResource(R.drawable.pic_bell_active)
			tvMenuNoti.setTypeface(null, Typeface.BOLD)
		}
		fun activeUser(tvMenuNoti: ImageView, tvMenuMe: TextView) {
			tvMenuNoti.setImageResource(R.drawable.pic_user_active)
			tvMenuMe.setTypeface(null, Typeface.BOLD)
		}
	}
}