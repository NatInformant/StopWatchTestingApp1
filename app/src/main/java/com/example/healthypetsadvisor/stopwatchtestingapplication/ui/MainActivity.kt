package com.example.healthypetsadvisor.stopwatchtestingapplication.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
            if (isConnected) {
                Toast.makeText(this, "Подключение есть", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Подключения нет", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestPermissions(vararg permissions: String) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.entries.forEach {
                Log.d("MainActivity", "${it.key} = ${it.value}")
            }
        }
        requestPermissionLauncher.launch(permissions.asList().toTypedArray())
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
