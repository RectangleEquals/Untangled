package com.rectangleequals.untangled.model.gamepad

import android.view.MotionEvent
import com.google.gson.JsonObject

data class AxisInfo(private val event: GamepadInputEvent) {
    val X: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_X)
    val Y: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_Y)
    val Z: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_Z)
    val Size: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_SIZE)
    val Orientation: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_ORIENTATION)
    val RX: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RX)
    val RY: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RY)
    val RZ: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RZ)
    val HatX: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_HAT_X)
    val HatY: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_HAT_Y)
    val LeftTrigger: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_LTRIGGER)
    val RightTrigger: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_RTRIGGER)
    val Wheel: Float? = event.motionEvent?.getAxisValue(MotionEvent.AXIS_WHEEL)

    fun toJson(): String {
        val json = JsonObject()
        json.addProperty("X", X)
        json.addProperty("Y", Y)
        json.addProperty("Z", Z)
        json.addProperty("Size", Size)
        json.addProperty("Orientation", Orientation)
        json.addProperty("RX", RX)
        json.addProperty("RY", RY)
        json.addProperty("RZ", RZ)
        json.addProperty("HatX", HatX)
        json.addProperty("HatY", HatY)
        json.addProperty("LeftTrigger", LeftTrigger)
        json.addProperty("RightTrigger", RightTrigger)
        json.addProperty("Wheel", Wheel)
        return json.toString()
    }
}