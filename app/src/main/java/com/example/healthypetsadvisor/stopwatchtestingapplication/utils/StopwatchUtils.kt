package com.example.healthypetsadvisor.stopwatchtestingapplication.utils

import android.content.Context
import android.content.Intent
import com.example.healthypetsadvisor.stopwatchtestingapplication.service.OverlayService
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.STOPWATCH_START_TIME

object StopwatchUtils {
    fun getFormattedTime(ms: Long): String {
        val milliseconds = ms / 10 % 100
        val seconds = ms / 1000

        return "${if (seconds < 10) "00" else if (seconds < 100) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }
    fun triggerForegroundService(context: Context, stopwatchStartTime: Long = 0L) {
        Intent(context, OverlayService::class.java).apply {
            putExtra(STOPWATCH_START_TIME, stopwatchStartTime)
            context.startService(this)
        }
    }
}
