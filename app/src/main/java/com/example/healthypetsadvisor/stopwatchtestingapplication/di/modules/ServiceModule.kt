package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.healthypetsadvisor.stopwatchtestingapplication.R
import com.example.healthypetsadvisor.stopwatchtestingapplication.service.ServiceHelper
import com.example.healthypetsadvisor.stopwatchtestingapplication.utils.Constants.NOTIFICATION_CHANNEL_ID
import org.koin.dsl.module

object ServiceModule {
    val serviceModule = module {
        single<NotificationCompat.Builder> {
            NotificationCompat.Builder(get(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Stopwatch")
                .setContentText("000:00")
                .setSmallIcon(R.drawable.baseline_access_time_24)
                .setOngoing(true)
                .addAction(0, "Stop", ServiceHelper.stopPendingIntent(context = get()))
                .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(context = get()))
                .setContentIntent(ServiceHelper.clickPendingIntent(context = get()))
        }
        single<NotificationManager> {
            get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

}