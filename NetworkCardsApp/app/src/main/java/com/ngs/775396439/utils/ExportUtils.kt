package com.ngs.`775396439`.utils

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import com.ngs.`775396439`.data.entity.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Font
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportUtils(private val context: Context) {

    companion object {
        private const val FONT_SIZE_TITLE = 18f
        private const val FONT_SIZE_HEADER = 14f
        private const val FONT_SIZE_NORMAL = 12f
        private const val FONT_SIZE_SMALL = 10f
    }

    /**
     * تصدير البيانات إلى PDF
     */
    fun exportToPdf(
        title: String,
        data: List<Any>,
        dataType: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // إعداد الخطوط
            val titlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = FONT_SIZE_TITLE
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
            }

            val headerPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = FONT_SIZE_HEADER
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.RIGHT
            }

            val normalPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = FONT_SIZE_NORMAL
                textAlign = android.graphics.Paint.Align.RIGHT
            }

            val smallPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = FONT_SIZE_SMALL
                textAlign = android.graphics.Paint.Align.RIGHT
            }

            var yPosition = 50f

            // رسم العنوان
            canvas.drawText(title, 595f / 2, yPosition, titlePaint)
            yPosition += 40f

            // رسم التاريخ
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar"))
            val currentDate = dateFormat.format(Date())
            canvas.drawText("تاريخ التصدير: $currentDate", 550f, yPosition, smallPaint)
            yPosition += 30f

            // رسم البيانات حسب النوع
            when (dataType) {
                "packages" -> drawPackagesData(canvas, data as List<Package>, yPosition, headerPaint, normalPaint, smallPaint)
                "inventory" -> drawInventoryData(canvas, data as List<Inventory>, yPosition, headerPaint, normalPaint, smallPaint)
                "stores" -> drawStoresData(canvas, data as List<Store>, yPosition, headerPaint, normalPaint, smallPaint)
                "expenses" -> drawExpensesData(canvas, data as List<Expense>, yPosition, headerPaint, normalPaint, smallPaint)
                "sales" -> drawSalesData(canvas, data as List<Sale>, yPosition, headerPaint, normalPaint, smallPaint)
                "payments" -> drawPaymentsData(canvas, data as List<Payment>, yPosition, headerPaint, normalPaint, smallPaint)
            }

            pdfDocument.finishPage(page)

            // حفظ الملف
            val fileName = "${dataType}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            onSuccess(file.absolutePath)
        } catch (e: Exception) {
            onError("خطأ في تصدير PDF: ${e.message}")
        }
    }

    /**
     * تصدير البيانات إلى Excel
     */
    fun exportToExcel(
        title: String,
        data: List<Any>,
        dataType: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet(title)

            // إنشاء أنماط الخلايا
            val titleStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND
                alignment = org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER
                verticalAlignment = org.apache.poi.ss.usermodel.VerticalAlignment.CENTER
            }

            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.LIGHT_BLUE.index
                fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND
                alignment = org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER
                verticalAlignment = org.apache.poi.ss.usermodel.VerticalAlignment.CENTER
            }

            val normalStyle = workbook.createCellStyle().apply {
                alignment = org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT
                verticalAlignment = org.apache.poi.ss.usermodel.VerticalAlignment.CENTER
            }

            var rowIndex = 0

            // إضافة العنوان
            val titleRow = sheet.createRow(rowIndex++)
            val titleCell = titleRow.createCell(0)
            titleCell.setCellValue(title)
            titleCell.cellStyle = titleStyle
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5))

            // إضافة التاريخ
            val dateRow = sheet.createRow(rowIndex++)
            val dateCell = dateRow.createCell(0)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar"))
            dateCell.setCellValue("تاريخ التصدير: ${dateFormat.format(Date())}")
            dateCell.cellStyle = normalStyle
            sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 5))

            rowIndex++ // سطر فارغ

            // إضافة البيانات حسب النوع
            when (dataType) {
                "packages" -> addPackagesToExcel(sheet, data as List<Package>, rowIndex, headerStyle, normalStyle)
                "inventory" -> addInventoryToExcel(sheet, data as List<Inventory>, rowIndex, headerStyle, normalStyle)
                "stores" -> addStoresToExcel(sheet, data as List<Store>, rowIndex, headerStyle, normalStyle)
                "expenses" -> addExpensesToExcel(sheet, data as List<Expense>, rowIndex, headerStyle, normalStyle)
                "sales" -> addSalesToExcel(sheet, data as List<Sale>, rowIndex, headerStyle, normalStyle)
                "payments" -> addPaymentsToExcel(sheet, data as List<Payment>, rowIndex, headerStyle, normalStyle)
            }

            // حفظ الملف
            val fileName = "${dataType}_${System.currentTimeMillis()}.xlsx"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            workbook.close()
            outputStream.close()

            onSuccess(file.absolutePath)
        } catch (e: Exception) {
            onError("خطأ في تصدير Excel: ${e.message}")
        }
    }

    /**
     * تصدير البيانات إلى JSON
     */
    fun exportToJson(
        data: List<Any>,
        dataType: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val jsonData = when (dataType) {
                "packages" -> convertPackagesToJson(data as List<Package>)
                "inventory" -> convertInventoryToJson(data as List<Inventory>)
                "stores" -> convertStoresToJson(data as List<Store>)
                "expenses" -> convertExpensesToJson(data as List<Expense>)
                "sales" -> convertSalesToJson(data as List<Sale>)
                "payments" -> convertPaymentsToJson(data as List<Payment>)
                else -> "[]"
            }

            val fileName = "${dataType}_${System.currentTimeMillis()}.json"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            file.writeText(jsonData)

            onSuccess(file.absolutePath)
        } catch (e: Exception) {
            onError("خطأ في تصدير JSON: ${e.message}")
        }
    }

    // دوال مساعدة لرسم البيانات في PDF
    private fun drawPackagesData(
        canvas: android.graphics.Canvas,
        packages: List<Package>,
        startY: Float,
        headerPaint: android.graphics.Paint,
        normalPaint: android.graphics.Paint,
        smallPaint: android.graphics.Paint
    ) {
        var yPosition = startY

        // رسم العناوين
        canvas.drawText("اسم الباقة", 500f, yPosition, headerPaint)
        canvas.drawText("سعر التجزئة", 400f, yPosition, headerPaint)
        canvas.drawText("سعر الجملة", 300f, yPosition, headerPaint)
        canvas.drawText("سعر الموزعين", 200f, yPosition, headerPaint)
        canvas.drawText("التاريخ", 100f, yPosition, headerPaint)
        yPosition += 30f

        // رسم البيانات
        packages.forEach { package_ ->
            canvas.drawText(package_.name, 500f, yPosition, normalPaint)
            canvas.drawText(formatPrice(package_.retailPrice), 400f, yPosition, normalPaint)
            canvas.drawText(formatPrice(package_.wholesalePrice), 300f, yPosition, normalPaint)
            canvas.drawText(formatPrice(package_.distributorPrice), 200f, yPosition, normalPaint)
            canvas.drawText(package_.createdAt, 100f, yPosition, smallPaint)
            yPosition += 25f
        }
    }

    private fun drawInventoryData(
        canvas: android.graphics.Canvas,
        inventory: List<Inventory>,
        startY: Float,
        headerPaint: android.graphics.Paint,
        normalPaint: android.graphics.Paint,
        smallPaint: android.graphics.Paint
    ) {
        var yPosition = startY

        // رسم العناوين
        canvas.drawText("الباقة", 500f, yPosition, headerPaint)
        canvas.drawText("الكمية", 400f, yPosition, headerPaint)
        canvas.drawText("التاريخ", 200f, yPosition, headerPaint)
        yPosition += 30f

        // رسم البيانات
        inventory.forEach { item ->
            canvas.drawText("باقة ${item.packageId}", 500f, yPosition, normalPaint)
            canvas.drawText(item.quantity.toString(), 400f, yPosition, normalPaint)
            canvas.drawText(item.createdAt, 200f, yPosition, smallPaint)
            yPosition += 25f
        }
    }

    // دوال مساعدة لإضافة البيانات إلى Excel
    private fun addPackagesToExcel(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        packages: List<Package>,
        startRow: Int,
        headerStyle: CellStyle,
        normalStyle: CellStyle
    ) {
        var rowIndex = startRow

        // إضافة العناوين
        val headerRow = sheet.createRow(rowIndex++)
        headerRow.createCell(0).apply { setCellValue("اسم الباقة"); cellStyle = headerStyle }
        headerRow.createCell(1).apply { setCellValue("سعر التجزئة"); cellStyle = headerStyle }
        headerRow.createCell(2).apply { setCellValue("سعر الجملة"); cellStyle = headerStyle }
        headerRow.createCell(3).apply { setCellValue("سعر الموزعين"); cellStyle = headerStyle }
        headerRow.createCell(4).apply { setCellValue("التاريخ"); cellStyle = headerStyle }

        // إضافة البيانات
        packages.forEach { package_ ->
            val dataRow = sheet.createRow(rowIndex++)
            dataRow.createCell(0).apply { setCellValue(package_.name); cellStyle = normalStyle }
            dataRow.createCell(1).apply { setCellValue(formatPrice(package_.retailPrice)); cellStyle = normalStyle }
            dataRow.createCell(2).apply { setCellValue(formatPrice(package_.wholesalePrice)); cellStyle = normalStyle }
            dataRow.createCell(3).apply { setCellValue(formatPrice(package_.distributorPrice)); cellStyle = normalStyle }
            dataRow.createCell(4).apply { setCellValue(package_.createdAt); cellStyle = normalStyle }
        }
    }

    private fun addInventoryToExcel(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        inventory: List<Inventory>,
        startRow: Int,
        headerStyle: CellStyle,
        normalStyle: CellStyle
    ) {
        var rowIndex = startRow

        // إضافة العناوين
        val headerRow = sheet.createRow(rowIndex++)
        headerRow.createCell(0).apply { setCellValue("الباقة"); cellStyle = headerStyle }
        headerRow.createCell(1).apply { setCellValue("الكمية"); cellStyle = headerStyle }
        headerRow.createCell(2).apply { setCellValue("التاريخ"); cellStyle = headerStyle }

        // إضافة البيانات
        inventory.forEach { item ->
            val dataRow = sheet.createRow(rowIndex++)
            dataRow.createCell(0).apply { setCellValue("باقة ${item.packageId}"); cellStyle = normalStyle }
            dataRow.createCell(1).apply { setCellValue(item.quantity.toDouble()); cellStyle = normalStyle }
            dataRow.createCell(2).apply { setCellValue(item.createdAt); cellStyle = normalStyle }
        }
    }

    // دوال مساعدة لتحويل البيانات إلى JSON
    private fun convertPackagesToJson(packages: List<Package>): String {
        return packages.joinToString(",", "[", "]") { package_ ->
            """{"id":"${package_.id}","name":"${package_.name}","retailPrice":${package_.retailPrice ?: 0},"wholesalePrice":${package_.wholesalePrice ?: 0},"distributorPrice":${package_.distributorPrice ?: 0},"createdAt":"${package_.createdAt}","image":"${package_.image}"}"""
        }
    }

    private fun convertInventoryToJson(inventory: List<Inventory>): String {
        return inventory.joinToString(",", "[", "]") { item ->
            """{"id":"${item.id}","packageId":"${item.packageId}","quantity":${item.quantity},"createdAt":"${item.createdAt}"}"""
        }
    }

    // دوال مساعدة أخرى (مبسطة)
    private fun drawStoresData(canvas: android.graphics.Canvas, stores: List<Store>, startY: Float, headerPaint: android.graphics.Paint, normalPaint: android.graphics.Paint, smallPaint: android.graphics.Paint) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun drawExpensesData(canvas: android.graphics.Canvas, expenses: List<Expense>, startY: Float, headerPaint: android.graphics.Paint, normalPaint: android.graphics.Paint, smallPaint: android.graphics.Paint) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun drawSalesData(canvas: android.graphics.Canvas, sales: List<Sale>, startY: Float, headerPaint: android.graphics.Paint, normalPaint: android.graphics.Paint, smallPaint: android.graphics.Paint) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun drawPaymentsData(canvas: android.graphics.Canvas, payments: List<Payment>, startY: Float, headerPaint: android.graphics.Paint, normalPaint: android.graphics.Paint, smallPaint: android.graphics.Paint) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun addStoresToExcel(sheet: org.apache.poi.ss.usermodel.Sheet, stores: List<Store>, startRow: Int, headerStyle: CellStyle, normalStyle: CellStyle) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun addExpensesToExcel(sheet: org.apache.poi.ss.usermodel.Sheet, expenses: List<Expense>, startRow: Int, headerStyle: CellStyle, normalStyle: CellStyle) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun addSalesToExcel(sheet: org.apache.poi.ss.usermodel.Sheet, sales: List<Sale>, startRow: Int, headerStyle: CellStyle, normalStyle: CellStyle) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun addPaymentsToExcel(sheet: org.apache.poi.ss.usermodel.Sheet, payments: List<Payment>, startRow: Int, headerStyle: CellStyle, normalStyle: CellStyle) {
        // تنفيذ مشابه للبيانات الأخرى
    }

    private fun convertStoresToJson(stores: List<Store>): String {
        return stores.joinToString(",", "[", "]") { store ->
            """{"id":"${store.id}","name":"${store.name}","priceType":"${store.priceType}","createdAt":"${store.createdAt}"}"""
        }
    }

    private fun convertExpensesToJson(expenses: List<Expense>): String {
        return expenses.joinToString(",", "[", "]") { expense ->
            """{"id":"${expense.id}","type":"${expense.type}","amount":${expense.amount},"notes":"${expense.notes}","date":"${expense.date}","addLater":${expense.addLater}}"""
        }
    }

    private fun convertSalesToJson(sales: List<Sale>): String {
        return sales.joinToString(",", "[", "]") { sale ->
            """{"id":"${sale.id}","storeId":"${sale.storeId}","packageId":"${sale.packageId}","reason":"${sale.reason}","quantity":${sale.quantity},"amount":${sale.amount},"pricePerUnit":${sale.pricePerUnit},"total":${sale.total},"date":"${sale.date}"}"""
        }
    }

    private fun convertPaymentsToJson(payments: List<Payment>): String {
        return payments.joinToString(",", "[", "]") { payment ->
            """{"id":"${payment.id}","storeId":"${payment.storeId}","amount":${payment.amount},"notes":"${payment.notes}","date":"${payment.date}"}"""
        }
    }

    private fun formatPrice(price: Double?): String {
        return if (price != null && price > 0) {
            String.format("%,.0f", price)
        } else {
            "0"
        }
    }
}