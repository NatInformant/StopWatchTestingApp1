package com.example.healthypetsadvisor.stopwatchtestingapplication.ui.test

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class TestFragment : Fragment(R.layout.fragment_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerUiListener()
    }

    private fun registerUiListener() {
        scannerLauncher.launch(
            ScanOptions().setPrompt("Scan Qr Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setBeepEnabled(false)
        )
    }

    private val scannerLauncher = registerForActivityResult(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), result.contents, Toast.LENGTH_SHORT).show()
        }

    }
}