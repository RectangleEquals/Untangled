package com.rectangleequals.untangled.model.gamepad

import com.google.gson.JsonObject
import com.rectangleequals.untangled.model.SharedData

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