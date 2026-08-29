package com.kuabhy.pdfprint

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.io.FileOutputStream

class MainActivity : Activity() {

    private val PDF_PICKER = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 40, 40, 40)

        val title = TextView(this)
        title.text = "PDF-Print"
        title.textSize = 30f

        val subtitle = TextView(this)
        subtitle.text = "Print your PDF documents easily"
        subtitle.textSize = 18f

        val button = Button(this)
        button.text = "Select PDF"

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, PDF_PICKER)
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(button)

        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PDF_PICKER && resultCode == RESULT_OK) {
            val uri = data?.data

            if (uri != null) {
                printPDF(uri)
            }
        }
    }

    private fun printPDF(uri: Uri) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val printAdapter = object : PrintDocumentAdapter() {

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder("PDF-Print")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build()

                callback.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<PageRange>,
                destination: ParcelFileDescriptor,
                cancellationSignal: CancellationSignal,
                callback: WriteResultCallback
            ) {
                try {
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(destination.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }

                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

                } catch (e: Exception) {
                    callback.onWriteFailed(e.message)
                }
            }
        }

        printManager.print(
            "PDF-Print",
            printAdapter,
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT)
                .build()
        )
    }
}
