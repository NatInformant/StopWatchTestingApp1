package com.example.healthypetsadvisor.stopwatchtestingapplication.ui

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.main.MainFragment
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.NetworkUtils


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkUtils.getNetworkLiveData(this).observe(this) { isConnected ->
            if (isConnected){
                Toast.makeText(this,"Подключение есть", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"Подключения нет", Toast.LENGTH_SHORT).show()
            }
        }
    }
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
