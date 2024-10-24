package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val mInterval = 1
    private var mHandler: Handler? = null
    private var timeInMiliSeconds = 0L
    private var startButtonClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initStopWatch()
        binding!!.resetButton.setOnClickListener {
            stopTimer()
            resetTimerView()
        }
        binding!!.startOrStopTextView.setOnClickListener{
            startOrStopButtonClicked(it)
        }
    }

    private fun initStopWatch() {
        binding?.textViewStopWatch?.text = "00:00:00"
    }

    private fun resetTimerView() {
        timeInMiliSeconds = 0
        startButtonClicked = false
        binding?.startOrStopTextView?.text = "Start"
        initStopWatch()
    }

    private fun startOrStopButtonClicked(v: View) {
        if (!startButtonClicked) {
            startTimer()
            startTimerView()
        } else {
            stopTimer()
            stopTimerView()
        }
    }

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun startTimerView() {
        binding?.startOrStopTextView?.text = "Stop"
        startButtonClicked = true
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    private fun stopTimerView() {
        binding?.startOrStopTextView?.text = "Resume"
        startButtonClicked = false
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInMiliSeconds += 1
                Log.e("timeInSeconds", timeInMiliSeconds.toString())
                updateStopWatchView(timeInMiliSeconds*1000)
            } finally {
                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun updateStopWatchView(timeInMiliSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInMiliSeconds))
        Log.e("formattedTime", formattedTime)
        binding?.textViewStopWatch?.text = formattedTime
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }
}
