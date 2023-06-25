package com.rectangleequals.untangled

import android.view.KeyEvent
import android.view.MotionEvent
import java.util.zip.Deflater
import com.google.gson.*

private val gson: Gson = Gson()

data class ControllerState(
    val event: GamepadInputEvent
) {
    // General
    var eventTime: Long? = event.motionEvent?.eventTime
        set(value) {
            field = value
            if(value != null)
                SharedData.lastEventTime = value
        }
        get() {
            if (field == null && event.motionEvent == null) {
                field = ++SharedData.lastEventTime
            }
            return field
        }

    // Axis
    var AxisX: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_X)
    var AxisY: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_Y)
    var AxisZ: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_Z)
    var AxisSize: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_SIZE)
    var AxisOrientation: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_ORIENTATION)
    var AxisRX: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RX)
    var AxisRY: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RY)
    var AxisRZ: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RZ)
    var AxisHatX: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_HAT_X)
    var AxisHatY: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_HAT_Y)
    var AxisLeftTrigger: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_LTRIGGER)
    var AxisRightTrigger: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RTRIGGER)
    var AxisWheel: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_WHEEL)

    // Buttons
    val ButtonMenu: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_MENU
    val ButtonA: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_A
    val ButtonB: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_B
    val ButtonC: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_C
    val ButtonX: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_X
    val ButtonY: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_Y
    val ButtonZ: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_Z
    val ButtonLeftShoulder: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_L1
    val ButtonRightShoulder: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_R1
    val ButtonLeftStick: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL
    val ButtonRightStick: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR
    val ButtonStart: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_START
    val ButtonSelect: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_SELECT
    val ButtonMode: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_MODE
}

data class SerializedControllerState(
    val controllerState: ControllerState
) {
    val serializedData: ByteArray

    init {
        serializedData = encodeAndCompress(controllerState)
    }

    private fun encodeAndCompress(controllerState: ControllerState): ByteArray {
        val json: String = gson.toJson(controllerState)
        return compress(json.toByteArray())
    }

    private fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()
        val compressedData = ByteArray(data.size)
        val compressedSize = deflater.deflate(compressedData)
        return compressedData.copyOf(compressedSize)
    }
}