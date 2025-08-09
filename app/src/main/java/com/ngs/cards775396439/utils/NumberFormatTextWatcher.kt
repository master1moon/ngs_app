package com.ngs.cards775396439.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.util.*

class NumberFormatTextWatcher : TextWatcher {

    private val decimalFormat = DecimalFormat("#,###")
    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        val str = s.toString()
        val cleanString = str.replace(Regex("[^\\d]"), "")

        if (cleanString.isNotEmpty()) {
            val parsed = cleanString.toLong()
            val formatted = decimalFormat.format(parsed)
            s?.replace(0, s.length, formatted)
        }

        isFormatting = false
    }

    companion object {
        fun applyTo(editText: EditText) {
            editText.addTextChangedListener(NumberFormatTextWatcher())
        }

        fun removeFormatting(text: String): String {
            return text.replace(Regex("[^\\d]"), "")
        }

        fun formatNumber(number: Long): String {
            val decimalFormat = DecimalFormat("#,###")
            return decimalFormat.format(number)
        }
    }
}