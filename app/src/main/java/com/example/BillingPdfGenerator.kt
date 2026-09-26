package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BillingPdfGenerator {

    data class PdfResult(
        val file: File,
        val success: Boolean,
        val message: String
    )

    fun generatePdf(
        context: Context,
        billedBy: String = "",
        heading: String,
        items: List<BillingItem>,
        totalPrice: Double
    ): PdfResult {
        val pdfDocument = PdfDocument()

        // Standard A4 dimensions in points (72 points per inch)
        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        paint.isAntiAlias = true

        val margin = 36f
        val contentWidth = pageWidth - (margin * 2)

        // 1. Header Banner Background
        paint.color = Color.parseColor("#0F172A")
        val headerRect = RectF(margin, 36f, margin + contentWidth, 116f)
        canvas.drawRoundRect(headerRect, 12f, 12f, paint)

        // Gold Accent Border
        val borderPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.parseColor("#F59E0B")
        }
        canvas.drawRoundRect(headerRect, 12f, 12f, borderPaint)

        // Name in Bold (or Default if empty)
        val displayName = billedBy.trim().ifBlank { "NAME" }
        paint.color = Color.parseColor("#FBBF24")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 17f
        canvas.drawText(displayName, margin + 18f, 68f, paint)

        paint.color = Color.parseColor("#38BDF8")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10.5f
        val subTitle = "SMART BILLING & ITEM LIST INVOICE"
        canvas.drawText(subTitle, margin + 18f, 88f, paint)

        // Date and Time (Right aligned)
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDate = sdf.format(Date())
        paint.color = Color.parseColor("#94A3B8")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Date: $currentDate", margin + contentWidth - 18f, 78f, paint)
        paint.textAlign = Paint.Align.LEFT

        // 2. User Defined Main Heading
        var currentY = 135f

        paint.color = Color.parseColor("#1E293B")
        val headingBg = RectF(margin, currentY, margin + contentWidth, currentY + 36f)
        canvas.drawRoundRect(headingBg, 8f, 8f, paint)

        paint.color = Color.parseColor("#F8FAFC")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 15f
        canvas.drawText(heading.ifBlank { "Billing & Item List" }, margin + 16f, currentY + 23f, paint)

        // 3. Table Header
        currentY += 44f
        val tableTop = currentY
        val colSnWidth = 44f
        val colQtyWidth = 48f
        val colRateWidth = 85f
        val colTotalWidth = 95f
        val colNameWidth = contentWidth - colSnWidth - colQtyWidth - colRateWidth - colTotalWidth

        paint.color = Color.parseColor("#0EA5E9") // Cyan Header
        val thRect = RectF(margin, tableTop, margin + contentWidth, tableTop + 28f)
        canvas.drawRoundRect(thRect, 6f, 6f, paint)

        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10.5f

        // Column Titles
        // S.N
        canvas.drawText("S.N", margin + 12f, tableTop + 18f, paint)
        // Name
        canvas.drawText("Saman Ka Naam", margin + colSnWidth + 8f, tableTop + 18f, paint)
        // Quantity
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Quantity", margin + colSnWidth + colNameWidth + (colQtyWidth / 2f), tableTop + 18f, paint)
        // Price (₹)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Price (₹)", margin + colSnWidth + colNameWidth + colQtyWidth + colRateWidth - 8f, tableTop + 18f, paint)
        // Kul Price (₹)
        canvas.drawText("Kul Price (₹)", margin + contentWidth - 12f, tableTop + 18f, paint)
        paint.textAlign = Paint.Align.LEFT

        currentY += 28f
        val rowHeight = 24f

        // 4. Table Rows
        items.forEachIndexed { index, item ->
            // Zebra striping
            paint.color = if (index % 2 == 0) Color.parseColor("#F8FAFC") else Color.parseColor("#F1F5F9")
            val rowRect = RectF(margin, currentY, margin + contentWidth, currentY + rowHeight)
            canvas.drawRect(rowRect, paint)

            // Row text
            paint.color = Color.parseColor("#0F172A")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f

            // S.N
            canvas.drawText("${index + 1}", margin + 14f, currentY + 16f, paint)

            // Name
            val displayNameItem = if (item.name.length > 30) item.name.take(28) + "..." else item.name
            canvas.drawText(displayNameItem, margin + colSnWidth + 8f, currentY + 16f, paint)

            // Quantity with unit
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(item.formattedQuantity, margin + colSnWidth + colNameWidth + (colQtyWidth / 2f), currentY + 16f, paint)

            // Unit Price (Rate per kg / L / Pc)
            paint.textAlign = Paint.Align.RIGHT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(item.rateLabel, margin + colSnWidth + colNameWidth + colQtyWidth + colRateWidth - 8f, currentY + 16f, paint)

            // Kul Price (Total Price)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(Locale.US, "₹ %.2f", item.totalPrice), margin + contentWidth - 12f, currentY + 16f, paint)
            paint.textAlign = Paint.Align.LEFT

            currentY += rowHeight
        }

        // 5. Total Summary Row
        currentY += 6f
        paint.color = Color.parseColor("#1E293B")
        val totalRect = RectF(margin, currentY, margin + contentWidth, currentY + 36f)
        canvas.drawRoundRect(totalRect, 8f, 8f, paint)

        // Total Items on Left
        paint.color = Color.parseColor("#FBBF24")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11.5f
        canvas.drawText("Total Items: ${items.size}", margin + 14f, currentY + 22f, paint)

        // Total Price on Right
        paint.color = Color.parseColor("#38BDF8")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13.5f
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(String.format(Locale.US, "Grand Total: ₹ %.2f", totalPrice), margin + contentWidth - 14f, currentY + 22f, paint)
        paint.textAlign = Paint.Align.LEFT

        // 6. Footer Note
        val footerY = pageHeight - 40f
        paint.color = Color.parseColor("#94A3B8")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        val footerText = if (billedBy.isNotBlank()) billedBy.trim() else "SMART BILLING & ITEM LIST INVOICE"
        canvas.drawText(footerText, pageWidth / 2f, footerY, paint)

        pdfDocument.finishPage(page)

        // Save File
        val safeTitle = heading.replace(Regex("[^a-zA-Z0-9]"), "_").take(20).ifEmpty { "Bill" }
        val fileName = "${safeTitle}_${System.currentTimeMillis()}.pdf"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val pdfFile = File(storageDir, fileName)

        return try {
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            PdfResult(pdfFile, true, "PDF downloaded successfully: $fileName")
        } catch (e: Exception) {
            pdfDocument.close()
            PdfResult(pdfFile, false, "Failed to create PDF: ${e.localizedMessage}")
        }
    }

    fun openOrSharePdf(context: Context, file: File, action: String = Intent.ACTION_VIEW) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = if (action == Intent.ACTION_SEND) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, file.name)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            val chooser = Intent.createChooser(intent, if (action == Intent.ACTION_SEND) "Share PDF" else "Open PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback or toast
        }
    }
}
