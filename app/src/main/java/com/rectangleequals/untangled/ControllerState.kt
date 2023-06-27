package com.rectangleequals.untangled

import android.view.KeyEvent
import android.view.MotionEvent
import java.util.zip.Deflater
import com.google.gson.*
import com.rectangleequals.untangled.ui.GroupContainer
import com.rectangleequals.untangled.ui.GroupRow

private val gson: Gson = Gson()

data class ControllerState(
    val event: GamepadInputEvent,
) {
    val General: GeneralInfo by lazy { GeneralInfo(event) }
    val Axis: AxisInfo by lazy { AxisInfo(event) }
    val Buttons: ButtonInfo by lazy { ButtonInfo(event) }

    fun toUI(): List<GroupContainer> {
        return mutableListOf<GroupContainer>(
            GroupContainer("General", 1, listOf(
                GroupRow("Event Time:", General.eventTime.toString())
            )),
            GroupContainer("Axis", 10, listOf(
                GroupRow("X:", Axis.X.toString()),
                GroupRow("Y:", Axis.Y.toString()),
                GroupRow("Z:", Axis.Z.toString()),
                GroupRow("Size:", Axis.Size.toString()),
                GroupRow("Orientation:", Axis.Orientation.toString()),
                GroupRow("RX:", Axis.RX.toString()),
                GroupRow("RY:", Axis.RY.toString()),
                GroupRow("RZ:", Axis.RZ.toString()),
                GroupRow("HatX:", Axis.HatX.toString()),
                GroupRow("HatY:", Axis.HatY.toString()),
                GroupRow("LeftTrigger:", Axis.LeftTrigger.toString()),
                GroupRow("RightTrigger:", Axis.RightTrigger.toString()),
                GroupRow("Wheel", Axis.Wheel.toString())
            )),
            GroupContainer("Buttons", 6, listOf(
                GroupRow("A:", Buttons.A.toString()),
                GroupRow("B:", Buttons.B.toString()),
                GroupRow("C:", Buttons.C.toString()),
                GroupRow("X:", Buttons.X.toString()),
                GroupRow("Y:", Buttons.Y.toString()),
                GroupRow("Z:", Buttons.Z.toString()),
                GroupRow("LeftShoulder:", Buttons.LeftShoulder.toString()),
                GroupRow("RightShoulder:", Buttons.RightShoulder.toString()),
                GroupRow("LeftStick:", Buttons.LeftStick.toString()),
                GroupRow("RightStick:", Buttons.RightStick.toString()),
                GroupRow("Start:", Buttons.Start.toString()),
                GroupRow("Select:", Buttons.Select.toString()),
                GroupRow("Mode:", Buttons.Mode.toString())
            ))
        )
    }

    fun toJson(): String {
        val json = JsonObject()
        json.add("General", gson.toJsonTree(General.toJson()))
        json.add("Axis", gson.toJsonTree(Axis.toJson()))
        json.add("Buttons", gson.toJsonTree(Buttons.toJson()))
        return json.toString()
    }
}

data class GeneralInfo(private val event: GamepadInputEvent) {
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

    fun toJson(): String {
        val json = JsonObject()
        json.addProperty("eventTime", eventTime)
        return json.toString()
    }
}

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

data class SerializedControllerState(
    val controllerState: ControllerState
) {
    val serializedData: ByteArray

    init {
        serializedData = serializeAndCompress(controllerState)
    }

    private fun serializeAndCompress(controllerState: ControllerState): ByteArray {
        val json: String = controllerState.toJson()
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