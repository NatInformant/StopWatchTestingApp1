package com.example.healthypetsadvisor.stopwatchtestingapplication.utils

object StopwatchUtils {
    fun getFormattedTime(ms: Long): String {
        val milliseconds = ms / 10 % 100
        val seconds = ms / 1000

        return "${if (seconds < 10) "00" else if (seconds < 100) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }
}