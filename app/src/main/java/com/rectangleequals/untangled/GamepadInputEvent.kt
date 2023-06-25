package com.rectangleequals.untangled

import android.view.KeyEvent
import android.view.MotionEvent

class GamepadInputEvent(motionEvent: MotionEvent?, keyEvent: KeyEvent?) {
    val motionEvent: MotionEvent? = motionEvent
    val keyEvent: KeyEvent? = keyEvent
}