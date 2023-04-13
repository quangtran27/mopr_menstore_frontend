package com.mopr.menstore.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Formatter {
	companion object {
		fun formatVNDAmount(amount: Long): String {
			val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
			symbols.groupingSeparator = '.'
			symbols.decimalSeparator = ','
			val decimalFormat = DecimalFormat("#,### đ", symbols)
			return decimalFormat.format(amount)
		}
	}
}