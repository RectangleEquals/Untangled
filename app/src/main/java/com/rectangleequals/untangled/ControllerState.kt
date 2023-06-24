package com.rectangleequals.untangled

import android.os.Build
import android.util.Base64
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.Deflater

data class ControllerState(
    val event: MotionEvent? = null
) {
    var eventTime: Long? = event?.eventTime
    var AxisHatX: Float? = event?.getAxisValue(MotionEvent.AXIS_HAT_X)
    var AxisHatY: Float? = event?.getAxisValue(MotionEvent.AXIS_HAT_Y)
    var AxisRX: Float? = event?.getAxisValue(MotionEvent.AXIS_RX)
    var AxisRY: Float? = event?.getAxisValue(MotionEvent.AXIS_RY)
    var AxisRZ: Float? = event?.getAxisValue(MotionEvent.AXIS_RZ)
    var AxisBrake: Float? = event?.getAxisValue(MotionEvent.AXIS_BRAKE)
    var AxisDistance: Float? = event?.getAxisValue(MotionEvent.AXIS_DISTANCE)
    var AxisGas: Float? = event?.getAxisValue(MotionEvent.AXIS_GAS)
    var AxisGeneric: List<Float?> = listOf(
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_1),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_2),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_2),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_3),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_4),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_5),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_6),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_7),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_8),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_9),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_10),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_11),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_12),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_13),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_14),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_15),
        event?.getAxisValue(MotionEvent.AXIS_GENERIC_16)
    )
    var AxisHScroll: Float? = event?.getAxisValue(MotionEvent.AXIS_HSCROLL)
    var AxisVScroll: Float? = event?.getAxisValue(MotionEvent.AXIS_VSCROLL)
    var AxisLTrigger: Float? = event?.getAxisValue(MotionEvent.AXIS_LTRIGGER)
    var AxisRTrigger: Float? = event?.getAxisValue(MotionEvent.AXIS_RTRIGGER)
    var AxisOrientation: Float? = event?.getAxisValue(MotionEvent.AXIS_ORIENTATION)
    var AxisPressure: Float? = event?.getAxisValue(MotionEvent.AXIS_PRESSURE)
    var AxisRelativeX: Float? = event?.getAxisValue(MotionEvent.AXIS_RELATIVE_X)
    var AxisRelativeY: Float? = event?.getAxisValue(MotionEvent.AXIS_RELATIVE_Y)
    var AxisRudder: Float? = event?.getAxisValue(MotionEvent.AXIS_RUDDER)
    var AxisX: Float? = event?.getAxisValue(MotionEvent.AXIS_X)
    var AxisY: Float? = event?.getAxisValue(MotionEvent.AXIS_Y)
    var AxisZ: Float? = event?.getAxisValue(MotionEvent.AXIS_Z)
    @RequiresApi(Build.VERSION_CODES.O)
    var AxisScroll: Float? = event?.getAxisValue(MotionEvent.AXIS_SCROLL)
    var AxisSize: Float? = event?.getAxisValue(MotionEvent.AXIS_SIZE)
    var AxisThrottle: Float? = event?.getAxisValue(MotionEvent.AXIS_THROTTLE)
    var AxisTilt: Float? = event?.getAxisValue(MotionEvent.AXIS_TILT)
    var AxisToolMajor: Float? = event?.getAxisValue(MotionEvent.AXIS_TOOL_MAJOR)
    var AxisToolMinor: Float? = event?.getAxisValue(MotionEvent.AXIS_TOOL_MINOR)
    var AxisWheel: Float? = event?.getAxisValue(MotionEvent.AXIS_WHEEL)
}

data class SerializedControllerState(
    val controllerState: ControllerState
) {
    val serializedData: ByteArray

    init {
        serializedData = encodeAndCompress(controllerState)
    }

    private fun encodeAndCompress(controllerState: ControllerState): ByteArray {
        val json: String = Gson().toJson(controllerState)
        //val encodedBytes = Base64.encode(json.toByteArray(), Base64.DEFAULT)
        return compress(json.toByteArray())
    }

    private fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()
        val compressedData = ByteArray(data.size)
        val compressedSize = deflater.deflate(compressedData)
        return compressedData.copyOf(compressedSize)

/*
        val byteArrayOutputStream = ByteArrayOutputStream()
        val deflaterOutputStream = DeflaterOutputStream(byteArrayOutputStream)
        deflaterOutputStream.write(data)
        deflaterOutputStream.close()
        return byteArrayOutputStream.toByteArray()
*/
    }
}