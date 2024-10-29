package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val stopwatchUpdaterDelay = 30L
    private var stopwatchHandler: Handler? = null
    private var timeInMiliSeconds = 0L
    private var isStopwatchRunning = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initStopWatch()
        binding!!.resetButton.setOnClickListener {
            stopStopwatch()
            resetTimerView()
        }
        binding!!.startOrStopTextView.setOnClickListener {
            startOrStopButtonClicked(it)
        }
    }

    private fun initStopWatch() {
        binding?.textViewStopWatch?.text = "000:00"
    }

    private fun resetTimerView() {
        timeInMiliSeconds = 0
        isStopwatchRunning = false
        binding?.startOrStopTextView?.text = "Start"
        initStopWatch()
    }

    private fun startOrStopButtonClicked(v: View) {
        if (!isStopwatchRunning) {
            startStopwatch()
            startStopwatchView()
        } else {
            stopStopwatch()
            stopStopwatchView()
        }
    }

    private fun startStopwatch() {
        previousTime = System.currentTimeMillis()
        stopwatchHandler = Handler(Looper.getMainLooper())
        stopwatchUpdater.run()
    }

    private fun startStopwatchView() {
        binding?.startOrStopTextView?.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatch() {
        stopwatchHandler?.removeCallbacks(stopwatchUpdater)
    }

    private fun stopStopwatchView() {
        binding?.startOrStopTextView?.text = "Resume"
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
        binding?.textViewStopWatch?.text = formattedTime
    }

    private fun getFormattedStopWatch(ms: Long): String {
        val milliseconds = ms/10 % 100
        val seconds = ms/1000

        return "${if (seconds < 10) "00" else if (seconds < 100) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }
    override fun onDestroy() {
        super.onDestroy()
        stopStopwatch()
    }
}
