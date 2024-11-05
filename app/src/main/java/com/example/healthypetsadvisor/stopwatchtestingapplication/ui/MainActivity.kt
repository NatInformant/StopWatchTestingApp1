package com.example.healthypetsadvisor.stopwatchtestingapplication.ui

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main.MainFragment


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (event.action != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event)
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val navHostFragment: Fragment? =
                    supportFragmentManager.fragments[0]
                (navHostFragment?.childFragmentManager?.primaryNavigationFragment
                        as? MainFragment)?.startOrStopButtonClicked()
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }
}
