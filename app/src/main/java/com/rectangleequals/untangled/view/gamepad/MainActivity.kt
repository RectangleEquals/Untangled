package com.rectangleequals.untangled.view.gamepad

import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.rectangleequals.untangled.R
import com.rectangleequals.untangled.controller.gamepad.GamepadInfoController
import com.rectangleequals.untangled.model.gamepad.GamepadInputEvent

class MainActivity : AppCompatActivity() {
    private lateinit var controllerInfoLayout: LinearLayout
    private lateinit var gamepadInfoController: GamepadInfoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_gamepad_info)
        controllerInfoLayout = findViewById(R.id.ControllerInfoLayout)
        gamepadInfoController = GamepadInfoController(this, controllerInfoLayout)
        gamepadInfoController.createViews()
        gamepadInfoController.updateData()
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
            || event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            gamepadInfoController.updateData(GamepadInputEvent(event, null))
            return true
        }
        return super.onGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            gamepadInfoController.updateData(GamepadInputEvent(null, event))
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}