package com.mopr.menstore.utils

import android.content.res.Resources
import android.util.TypedValue

class Converter {
	companion object {
		fun dpToPx(dp: Int): Int {
			return TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
				Resources.getSystem().displayMetrics
			).toInt()
		}
	}
}