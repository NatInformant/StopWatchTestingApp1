package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.StopwatchOverlayBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.INTENT_ACTION_NAME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_COROUTINE_DELAY
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_START_TIME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STATE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STOP_TIME
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
    private val binding: StopwatchOverlayBinding by lazy {
        StopwatchOverlayBinding.inflate(LayoutInflater.from(this))
    }
    private var stopwatchStopTime = 0L
    private var stopwatchStartTime: Long = 0L
    private var stopwatchJob: Job? = null
    private var isStopwatchRunning = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopwatchStartTime = intent?.getLongExtra(STOPWATCH_START_TIME, 0L) ?: 0L
        if (stopwatchStartTime == -1L) {
            setUpUiToStartStopwatch()
            destroyOverlayView()
        } else {
            createOverlayView()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createOverlayView() {
        stopwatchJob = getStopwatchJob()

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

    private fun destroyOverlayView() {
        stopwatchJob?.cancel()
        removeOverlayView()
    }

    private fun resumeStopwatchTime() {
        notifyFragment(StopwatchState.RESUME)
        stopwatchStartTime = stopwatchStartTime + System.currentTimeMillis() - stopwatchStopTime
        stopwatchJob = getStopwatchJob()
    }

    private fun getStopwatchJob() = CoroutineScope(Dispatchers.Default).launch {
        while (isActive) {
            stopwatchStopTime = System.currentTimeMillis()
            val currentStopwatchTime = stopwatchStopTime - stopwatchStartTime
            binding.stopwatchTextview.post {
                binding.stopwatchTextview.text = getFormattedTime(currentStopwatchTime)
            }
            /*updateStopwatchUi(currentStopwatchTime)*/
            delay(STOPWATCH_COROUTINE_DELAY)
        }
    }

    /*private suspend fun updateStopwatchUi(currentStopwatchTime: Long) {
        withContext(Dispatchers.Main) {
            binding.stopwatchTextview.post {
                binding.stopwatchTextview.text = getFormattedTime(currentStopwatchTime)
            }
        }
    }*/

    private fun setUpUiToStartStopwatch() {
        binding.resumeOrStopTextview.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchJob?.cancel()
        notifyFragment(StopwatchState.STOP, stopwatchStopTime)
    }

    private fun setUpUiToStopStopwatch() {
        binding.resumeOrStopTextview.text = "Resume"
        isStopwatchRunning = false
    }

    private fun resetStopwatch() {
        notifyFragment(StopwatchState.RESET)
        stopwatchJob?.cancel()
        windowManager.removeView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopwatchJob?.cancel()
        removeOverlayView()
    }

    private fun removeOverlayView() {
        if (binding.root.isAttachedToWindow) {
            windowManager.removeView(binding.root)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun notifyFragment(state: StopwatchState, stopwatchStopTime: Long = 0L) {
        val intent = Intent(INTENT_ACTION_NAME)
        intent.putExtra(STOPWATCH_STATE, state.name)
        if (stopwatchStopTime != 0L) intent.putExtra(STOPWATCH_STOP_TIME, stopwatchStopTime)

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
