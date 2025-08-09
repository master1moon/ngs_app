package com.ngs.`775396439`.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.util.*

class NumberFormatTextWatcher : TextWatcher {
    
    private var isFormatting = false
    private val decimalFormat = DecimalFormat("#,###")
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // لا نحتاج لتنفيذ أي شيء هنا
    }
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // لا نحتاج لتنفيذ أي شيء هنا
    }
    
    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return
        
        isFormatting = true
        
        try {
            val str = s.toString().replace(",", "")
            if (str.isNotEmpty()) {
                val number = str.toLong()
                val formatted = decimalFormat.format(number)
                s?.replace(0, s.length, formatted)
            }
        } catch (e: NumberFormatException) {
            // إذا كان النص ليس رقماً، اتركه كما هو
        }
        
        isFormatting = false
    }
    
    companion object {
        /**
         * تطبيق التنسيق على EditText
         */
        fun applyTo(editText: EditText) {
            editText.addTextChangedListener(NumberFormatTextWatcher())
        }
        
        /**
         * إزالة الفواصل من النص للحصول على الرقم الصحيح
         */
        fun removeFormatting(text: String): String {
            return text.replace(",", "")
        }
        
        /**
         * تنسيق رقم مع فواصل
         */
        fun formatNumber(number: Long): String {
            val decimalFormat = DecimalFormat("#,###")
            return decimalFormat.format(number)
        }
        
        /**
         * تنسيق رقم عشري مع فواصل
         */
        fun formatDecimal(number: Double): String {
            val decimalFormat = DecimalFormat("#,###.##")
            return decimalFormat.format(number)
        }
    }
}