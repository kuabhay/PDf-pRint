package com.kuabhy.pdfprint

import android.app.Activity

import android.graphics.pdf.PdfDocument
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

class MainActivity : Activity() {

    private val PDF_PICKER = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply {
            text = "PDF-Print"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Print your PDF documents easily"
            textSize = 16f
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Select PDF"
            setOnClickListener {
                selectPdf()
            }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(button)

        setContentView(layout)
    }

    private fun selectPdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(intent, PDF_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PDF_PICKER && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                printPdf(uri)
            }
        }
    }

    private fun printPdf(uri: Uri) {
        try {
            val file = File(cacheDir, "selected.pdf")

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            val printManager = getSystemService(PRINT_SERVICE) as PrintManager

            val adapter: PrintDocumentAdapter =
                PdfPrintAdapter(this, file)

            printManager.print(
                "PDF-Print",
                adapter,
                PrintAttributes.Builder().build()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class PdfPrintAdapter(
    private val activity: Activity,
    private val file: File
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        val info = PrintDocumentInfo.Builder(file.name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<android.print.PageRange>,
        destination: android.os.ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback
    ) {
        try {
            FileOutputStream(destination.fileDescriptor).use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            callback.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))

        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        }
    }
}
