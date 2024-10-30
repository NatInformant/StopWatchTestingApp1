package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val stopwatchUpdaterDelay = 30L
    private var stopwatchHandler: Handler? = null
    private var timeInMiliSeconds = 0L
    private var isStopwatchRunning = false
    private val stopwatchDefaultValue = "000:00"
    private val stopwatchListSize = 5;

    private val stopwatchListAdapter = StopwatchListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initStopwatchList()
        binding.resetButton.setOnClickListener {
            stopStopwatchTime()
            resetStopwatch()
        }
        binding.startOrStopTextView.setOnClickListener {
            startOrStopButtonClicked(it)
        }

        with(binding.stopwatchList) {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = stopwatchListAdapter
            itemAnimator = null
        }

        setContentView(binding.root)
    }

    private fun initStopwatchList() {
        stopwatchListAdapter.submitList(
            List(stopwatchListSize) { stopwatchDefaultValue }
        )
    }

    private fun resetStopwatch() {
        timeInMiliSeconds = 0
        isStopwatchRunning = false
        binding.startOrStopTextView.text = "Start"
        initStopwatchList()
    }

    private fun startOrStopButtonClicked(v: View) {
        if (!isStopwatchRunning) {
            startStopwatchTime()
            setUpUiToStartStopwatch()
        } else {
            stopStopwatchTime()
            setUpUiToStopStopwatch()
        }
    }

    private fun startStopwatchTime() {
        previousTime = System.currentTimeMillis()
        stopwatchHandler = Handler(Looper.getMainLooper())
        stopwatchUpdater.run()
    }

    private fun setUpUiToStartStopwatch() {
        binding.startOrStopTextView.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchHandler?.removeCallbacks(stopwatchUpdater)
    }

    private fun setUpUiToStopStopwatch() {
        binding.startOrStopTextView.text = "Resume"
        isStopwatchRunning = false
    }

    private var previousTime = 0L
    private var stopwatchUpdater: Runnable = object : Runnable {
        override fun run() {
            try {
                val currentTime = System.currentTimeMillis()
                timeInMiliSeconds += currentTime - previousTime
                previousTime = currentTime
                updateStopWatchView(timeInMiliSeconds)
            } finally {
                stopwatchHandler!!.postDelayed(this, stopwatchUpdaterDelay)
            }
        }
    }

    private fun updateStopWatchView(timeInMiliSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInMiliSeconds))
        stopwatchListAdapter.submitList(List(stopwatchListSize) { formattedTime })
    }

    private fun getFormattedStopWatch(ms: Long): String {
        val milliseconds = ms / 10 % 100
        val seconds = ms / 1000

        return "${if (seconds < 10) "00" else if (seconds < 100) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }

    override fun onDestroy() {
        super.onDestroy()
        stopStopwatchTime()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode

        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN-> {
                Toast.makeText(this, "Громкость переопределена", Toast.LENGTH_SHORT).show()
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                Toast.makeText(this, "Кнопка назад переопределена", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.dispatchKeyEvent(event)
        }
    }
}
