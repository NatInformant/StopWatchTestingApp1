package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.healthypetsadvisor.stopwatchtestingapplication.databinding.StopwatchOverlayBinding
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.MainActivity
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("ClickableViewAccessibility")
    private fun createOverlayView() {
        stopwatchJob = getStopwatchJob()

        binding.root.setOnTouchListener(
            object : View.OnTouchListener {
                private var initialX: Int = 0
                private var initialY: Int = 0
                private var initialTouchX: Float = 0f
                private var initialTouchY: Float = 0f

                override fun onTouch(p0: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            //when the drag is ended switching the state of the widget
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager.updateViewLayout(binding.root, params)
                            return true;
                        }
                    }
                    return false
                }
            })
        binding.stopwatchTextview.setOnClickListener {
            createMainActivityIntent().send(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
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

    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun destroyOverlayView() {
        stopwatchJob?.cancel()
        removeOverlayView()
    }

    private fun resumeStopwatchTime() {
        stopwatchStartTime = stopwatchStartTime + System.currentTimeMillis() - stopwatchStopTime
        notifyFragment(StopwatchState.RESUME)
        /*Log.w("Current stopwatch start time", stopwatchStartTime.toString())*/
        stopwatchJob = getStopwatchJob()
    }

    private fun getStopwatchJob() = CoroutineScope(Dispatchers.Default).launch {
        while (isActive) {
            stopwatchStopTime = System.currentTimeMillis()
            val currentStopwatchTime = stopwatchStopTime - stopwatchStartTime
            binding.stopwatchTextview.post {
                binding.stopwatchTextview.text = getFormattedTime(currentStopwatchTime)
            }
            /*Log.i("Current stopwatch stop time", stopwatchStopTime.toString())
            Log.v("Current stopwatch time", currentStopwatchTime.toString())*/
            delay(STOPWATCH_COROUTINE_DELAY)
        }
    }


    private fun setUpUiToStartStopwatch() {
        binding.resumeOrStopTextview.text = "Stop"
        isStopwatchRunning = true
    }

    private fun stopStopwatchTime() {
        stopwatchJob?.cancel()
        notifyFragment(StopwatchState.STOP)
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

    private fun notifyFragment(state: StopwatchState) {
        val intent = Intent(INTENT_ACTION_NAME)

        intent.putExtra(STOPWATCH_STATE, state.name)

        when (state) {
            StopwatchState.RESUME -> {
                intent.putExtra(STOPWATCH_START_TIME, stopwatchStartTime)
            }

            StopwatchState.STOP -> {
                intent.putExtra(STOPWATCH_STOP_TIME, stopwatchStopTime)
            }

            else -> {}
        }

        /*        Log.d("Sended stopwatch stop time", stopwatchStopTime.toString())*/
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
