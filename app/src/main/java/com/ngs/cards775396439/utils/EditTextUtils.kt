package com.ngs.cards775396439.utils

import android.widget.EditText

object EditTextUtils {

    /**
     * تطبيق تنسيق الأرقام مع فواصل على EditText
     */
    fun applyNumberFormatting(editText: EditText) {
        NumberFormatTextWatcher.applyTo(editText)
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
     * تعيين قيمة عشري مع تنسيق
     */
    fun setNumericValue(editText: EditText, value: Double) {
        editText.setText(NumberFormatTextWatcher.formatNumber(value.toLong()))
    }

    /**
     * تنسيق رقم مع فواصل
     */
    fun formatNumber(number: Long): String {
        return NumberFormatTextWatcher.formatNumber(number)
    }
}