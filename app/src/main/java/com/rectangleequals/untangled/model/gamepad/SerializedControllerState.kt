package com.rectangleequals.untangled.model.gamepad

import java.util.zip.Deflater

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