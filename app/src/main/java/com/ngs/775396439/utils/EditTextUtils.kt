package com.ngs.`775396439`.utils

import android.widget.EditText

object EditTextUtils {
    
    /**
     * تطبيق تنسيق الأرقام مع فواصل على EditText
     */
    fun applyNumberFormatting(editText: EditText) {
        NumberFormatTextWatcher.applyTo(editText)
    }
    
    /**
     * تطبيق تنسيق الأرقام العشرية مع فواصل على EditText
     */
    fun applyDecimalFormatting(editText: EditText) {
        editText.addTextChangedListener(DecimalFormatTextWatcher())
    }
    
    /**
     * الحصول على القيمة الرقمية من EditText مع إزالة التنسيق
     */
    fun getNumericValue(editText: EditText): String {
        return NumberFormatTextWatcher.removeFormatting(editText.text.toString())
    }
    
    /**
     * تعيين قيمة رقمية مع تنسيق
     */
    fun setNumericValue(editText: EditText, value: Long) {
        editText.setText(NumberFormatTextWatcher.formatNumber(value))
    }
    
    /**
     * تعيين قيمة عشرية مع تنسيق
     */
    fun setDecimalValue(editText: EditText, value: Double) {
        editText.setText(NumberFormatTextWatcher.formatDecimal(value))
    }
}

/**
 * TextWatcher للأرقام العشرية
 */
class DecimalFormatTextWatcher : NumberFormatTextWatcher() {
    
    private val decimalFormat = java.text.DecimalFormat("#,###.##")
    
    override fun afterTextChanged(s: android.text.Editable?) {
        if (isFormatting) return
        
        isFormatting = true
        
        try {
            val str = s.toString().replace(",", "")
            if (str.isNotEmpty()) {
                val number = str.toDouble()
                val formatted = decimalFormat.format(number)
                s?.replace(0, s.length, formatted)
            }
        } catch (e: NumberFormatException) {
            // إذا كان النص ليس رقماً، اتركه كما هو
        }
        
        isFormatting = false
    }
}