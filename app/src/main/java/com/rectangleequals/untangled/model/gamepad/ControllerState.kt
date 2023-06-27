package com.rectangleequals.untangled.model.gamepad

import com.google.gson.JsonObject
import com.rectangleequals.untangled.model.SharedData

data class ControllerState(val event: GamepadInputEvent) {
    val General: GeneralInfo by lazy { GeneralInfo(event) }
    val Axis: AxisInfo by lazy { AxisInfo(event) }
    val Buttons: ButtonInfo by lazy { ButtonInfo(event) }

    fun toUI(): List<GroupContainer> {
        return mutableListOf<GroupContainer>(
            GroupContainer("General", 1, listOf(
                GroupRow("Event Time:", General.eventTime.toString())
            )),
            GroupContainer("Axis", 8, listOf(
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
        json.add("General", SharedData.gson.toJsonTree(General.toJson()))
        json.add("Axis", SharedData.gson.toJsonTree(Axis.toJson()))
        json.add("Buttons", SharedData.gson.toJsonTree(Buttons.toJson()))
        return json.toString()
    }
}