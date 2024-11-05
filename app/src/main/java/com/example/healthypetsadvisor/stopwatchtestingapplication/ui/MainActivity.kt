package com.example.healthypetsadvisor.stopwatchtestingapplication.ui

import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthypetsadvisor.stopwatchtestingapplication.R


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (event.action != KeyEvent.ACTION_DOWN) {
            return  super.dispatchKeyEvent(event)
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                Toast.makeText(this, "Громкость переопределена", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }
}
