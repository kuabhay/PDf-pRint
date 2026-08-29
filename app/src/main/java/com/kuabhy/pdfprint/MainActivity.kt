package com.kuabhy.pdfprint

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 60, 40, 40)

        val title = TextView(this)
        title.text = "PDF-Print"
        title.textSize = 30f

        val subtitle = TextView(this)
        subtitle.text = "Print your PDF documents easily"
        subtitle.textSize = 18f

        val button = Button(this)
        button.text = "Select PDF"

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(button)

        setContentView(layout)
    }
}
