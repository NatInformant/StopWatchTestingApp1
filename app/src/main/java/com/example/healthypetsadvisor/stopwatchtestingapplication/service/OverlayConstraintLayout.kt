package com.example.healthypetsadvisor.stopwatchtestingapplication.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class OverlayConstraintLayout(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet), KoinComponent {
    private val windowManager: WindowManager by inject()
    private val params: WindowManager.LayoutParams by inject()

    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
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
                windowManager.updateViewLayout(this, params)
                return true
            }
        }
        return false
    }
}
