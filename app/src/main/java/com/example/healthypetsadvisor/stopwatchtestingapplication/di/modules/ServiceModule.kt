package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import android.app.Service.WINDOW_SERVICE
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import org.koin.dsl.module

object ServiceModule {
    val serviceModule = module {
        single<WindowManager.LayoutParams> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
                return@single WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
                )
            }
            else {
                //Не уверен, что такой вариант Норм,
                // возмонжно придётся кидать пользователю сообщение о том,
                // что на его версии ОС данная фича не поддерживается.
                WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            }
        }
        single<WindowManager> {
            get<Context>().getSystemService(WINDOW_SERVICE) as WindowManager
        }
    }

}
