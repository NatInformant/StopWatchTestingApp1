package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.ACTION_SERVICE_CANCEL
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.ACTION_SERVICE_START
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.ACTION_SERVICE_STOP
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.NOTIFICATION_ID
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_COROUTINE_DELAY
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_START_TIME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STATE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.StopwatchUtils.getFormattedTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StopwatchNotificationService : Service(), KoinComponent {
    val notificationManager: NotificationManager by inject()
    val notificationBuilder: NotificationCompat.Builder by inject()

    private val binder = StopwatchBinder()

    var stopwatchStartTime = 0L
    var currentState = StopwatchState.Idle
    private var stopwatchJob: Job? = null

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getStringExtra(STOPWATCH_STATE)) {
            StopwatchState.Started.name -> {
                setStopButton()
                startForegroundService()
                startStopwatch { miliSeconds ->
                    updateNotification(timeInMilis = miliSeconds)
                }
            }

            StopwatchState.Stopped.name -> {
                stopStopwatch()
                setResumeButton()
            }

            StopwatchState.Canceled.name -> {
                stopStopwatch()
                cancelStopwatch()
                stopForegroundService()
            }
        }
        intent?.action?.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    stopwatchStartTime = intent.getLongExtra(STOPWATCH_START_TIME, 0L)
                    startStopwatch { miliSeconds ->
                        updateNotification(timeInMilis = miliSeconds)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopStopwatch()
                    setResumeButton()
                }

                ACTION_SERVICE_CANCEL -> {
                    stopStopwatch()
                    cancelStopwatch()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startStopwatch(onTick: (h: Long) -> Unit) {
        currentState = StopwatchState.Started
        stopwatchJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val currentStopwatchTime = currentTime - stopwatchStartTime
                onTick(currentStopwatchTime)
                delay(STOPWATCH_COROUTINE_DELAY)
            }
        }
    }

    private fun stopStopwatch() {
        stopwatchJob?.cancel()
        currentState = StopwatchState.Stopped
    }

    private fun cancelStopwatch() {
        stopwatchStartTime = 0
        currentState = StopwatchState.Idle
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(timeInMilis: Long) {
        Log.d("NotificationSendEvent", getFormattedTime(timeInMilis))
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                getFormattedTime(timeInMilis)
            ).build()
        )
    }

    @SuppressLint("RestrictedApi")
    private fun setStopButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Stop",
                ServiceHelper.stopPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopwatchNotificationService = this@StopwatchNotificationService
    }
}