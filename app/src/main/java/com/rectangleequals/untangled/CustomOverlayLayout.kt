package com.rectangleequals.untangled

import android.content.Context
import android.util.AttributeSet
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class CustomOverlayLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val gamepadEventListeners: MutableList<(GamepadInputEvent) -> Boolean> = mutableListOf()

    fun addGamepadEventListener(listener: (GamepadInputEvent) -> Boolean) {
        gamepadEventListeners.add(listener)
    }

    fun removeGamepadEventListener(listener: (GamepadInputEvent) -> Boolean) {
        gamepadEventListeners.remove(listener)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
            || event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            for (listener in gamepadEventListeners) {
                if (listener(GamepadInputEvent(event, null))) {
                    return true
                }
            }
        } else {
            return false
        }
        return super.onGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            for (listener in gamepadEventListeners) {
                if (listener(GamepadInputEvent(null, event))) {
                    return true
                }
            }
        } else {
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}