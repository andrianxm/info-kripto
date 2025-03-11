package com.andrian.infokripto.helper

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatToRupiah(amount: String): String {
    return try {
        val parsedAmount = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        if (parsedAmount < BigDecimal.ONE) {
            "Rp ${parsedAmount.stripTrailingZeros().toPlainString()}"
        } else {
            formatRupiah.format(parsedAmount).replace(",00", "")
        }
    } catch (e: NumberFormatException) {
        amount
    }
}
