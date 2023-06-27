package com.rectangleequals.untangled.model.gamepad

import android.view.KeyEvent
import com.google.gson.JsonObject

data class ButtonInfo(private val event: GamepadInputEvent) {
    val Menu: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_MENU
    val A: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_A
    val B: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_B
    val C: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_C
    val X: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_X
    val Y: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_Y
    val Z: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_Z
    val LeftShoulder: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_L1
    val RightShoulder: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_R1
    val LeftStick: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_THUMBL
    val RightStick: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_THUMBR
    val Start: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_START
    val Select: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_SELECT
    val Mode: Boolean? = event.keyEvent?.keyCode == KeyEvent.KEYCODE_BUTTON_MODE

    fun toJson(): String {
        val json = JsonObject()
        json.addProperty("Menu", Menu)
        json.addProperty("A", A)
        json.addProperty("B", B)
        json.addProperty("C", C)
        json.addProperty("X", X)
        json.addProperty("Y", Y)
        json.addProperty("Z", Z)
        json.addProperty("LeftShoulder", LeftShoulder)
        json.addProperty("RightShoulder", RightShoulder)
        json.addProperty("LeftStick", LeftStick)
        json.addProperty("RightStick", RightStick)
        json.addProperty("Start", Start)
        json.addProperty("Select", Select)
        json.addProperty("Mode", Mode)
        return json.toString()
    }
}