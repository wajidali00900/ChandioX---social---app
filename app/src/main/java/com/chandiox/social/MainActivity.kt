package com.chandiox.social

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF0F172A.toInt())
            setPadding(50,50,50,50)
        }
        val title = TextView(this).apply {
            text = "ChandioX\nSocial App"
            textSize = 32f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0,0,0,50)
        }
        val subtitle = TextView(this).apply {
            text = "Welcome Wajid Ali!\nApp Built Successfully 🚀"
            textSize = 18f
            setTextColor(0xFF94A3B8.toInt())
            gravity = Gravity.CENTER
        }
        layout.addView(title)
        layout.addView(subtitle)
        setContentView(layout)
    }
}
