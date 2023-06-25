package com.rectangleequals.untangled

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

private const val TAG = "CustomOverlayLayout"

class CustomOverlayLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val gamepadEventListeners: MutableList<(MotionEvent) -> Boolean> = mutableListOf()

    fun addGamepadEventListener(listener: (MotionEvent) -> Boolean) {
        gamepadEventListeners.add(listener)
    }

    fun removeGamepadEventListener(listener: (MotionEvent) -> Boolean) {
        gamepadEventListeners.remove(listener)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            // Process the gamepad event here
            for (listener in gamepadEventListeners) {
                if (listener(event)) {
                    return true // If any listener handles the event, consume it
                }
            }
        } else {
            Log.v(TAG, "[GENERIC]: $event")
            return false // Return false to allow the event to be handled by other listeners
        }
        return super.dispatchGenericMotionEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}
