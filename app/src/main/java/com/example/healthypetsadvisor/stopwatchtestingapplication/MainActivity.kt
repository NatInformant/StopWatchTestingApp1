package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    private var stopwatchList = emptyList<String>()

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
        stopwatchListAdapter.submitList(stopwatchList)
        with(binding.stopwatchList){
            this.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
            this.adapter = stopwatchListAdapter
        }

        setContentView(binding.root)
    }

    private fun initStopwatchList() {
        stopwatchList = listOf(stopwatchDefaultValue, stopwatchDefaultValue)
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
        stopwatchList = listOf( formattedTime, formattedTime)
        stopwatchListAdapter.submitList(stopwatchList)
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
}
