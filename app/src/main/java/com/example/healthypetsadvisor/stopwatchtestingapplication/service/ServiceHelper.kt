package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.healthypetsadvisor.stopwatchtestingapplication.ui.MainActivity
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.CANCEL_REQUEST_CODE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.CLICK_REQUEST_CODE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.RESUME_REQUEST_CODE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_START_TIME
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_STATE
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOP_REQUEST_CODE


object ServiceHelper {

    private val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            0

    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }
        return PendingIntent.getActivity(
            context, CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, StopwatchNotificationService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Stopped.name)
        }
        return PendingIntent.getService(
            context, STOP_REQUEST_CODE, stopIntent, flag
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, StopwatchNotificationService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, StopwatchNotificationService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Canceled.name)
        }
        return PendingIntent.getService(
            context, CANCEL_REQUEST_CODE, cancelIntent, flag
        )
    }

    fun triggerForegroundService(context: Context, action: String, stopwatchStartTime: Long = 0L) {
        Intent(context, StopwatchNotificationService::class.java).apply {
            this.action = action
            putExtra(STOPWATCH_START_TIME, stopwatchStartTime)
            context.startService(this)
        }
    }
}