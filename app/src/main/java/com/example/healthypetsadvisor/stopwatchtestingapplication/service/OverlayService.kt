package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.StopwatchOverlayBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_COROUTINE_DELAY
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_START_TIME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.StopwatchUtils.getFormattedTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OverlayService : Service(), KoinComponent {
    private val windowManager: WindowManager by inject()
    private val params: WindowManager.LayoutParams by inject()
    private lateinit var binding: StopwatchOverlayBinding
    private var stopwatchStopTime = 0L
    private var stopwatchStartTime: Long = 0L
    private var stopwatchJob: Job? = null
    private var isStopwatchRunning = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopwatchStartTime = intent?.getLongExtra(STOPWATCH_START_TIME, 0L) ?: 0L
        createOverlayView()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createOverlayView() {
        // Создание кастомного окна, который будет отображаться поверх других приложений
        binding = StopwatchOverlayBinding.inflate(LayoutInflater.from(this))

        stopwatchJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val currentStopwatchTime = currentTime - stopwatchStartTime
                withContext(Dispatchers.Main) {
                    binding.stopwatchTextview.text = getFormattedTime(currentStopwatchTime)
                }
                delay(STOPWATCH_COROUTINE_DELAY)
            }
        }

        binding.resumeOrStopTextview.setOnClickListener {
            if (!isStopwatchRunning) {
                resumeStopwatchTime()
                setUpUiToStartStopwatch()
            } else {
                stopStopwatchTime()
                setUpUiToStopStopwatch()
            }
        }

        binding.resetButton.setOnClickListener {
            stopStopwatchTime()
            resetStopwatch()
        }

        windowManager.addView(binding.root, params)
    }

    private fun resumeStopwatchTime() {
        stopwatchStartTime = stopwatchStartTime + System.currentTimeMillis() - stopwatchStopTime
        stopwatchJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val currentStopwatchTime = currentTime - stopwatchStartTime
                withContext(Dispatchers.Main) {
                    binding.stopwatchTextview.text = getFormattedTime(currentStopwatchTime)
                }
                delay(STOPWATCH_COROUTINE_DELAY)
            }
        }
    }

    private fun setUpUiToStartStopwatch() {
        binding.resumeOrStopTextview.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchStopTime = System.currentTimeMillis()
        stopwatchJob?.cancel()
    }

    private fun setUpUiToStopStopwatch() {
        binding.resumeOrStopTextview.text = "Resume"
        isStopwatchRunning = false
    }

    private fun resetStopwatch() {
        windowManager.removeView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(binding.root)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
