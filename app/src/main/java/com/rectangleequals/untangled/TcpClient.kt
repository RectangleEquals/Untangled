package com.rectangleequals.untangled

import java.io.IOException
import java.io.OutputStream
import java.net.Socket

class TcpClient(
    private val activity: ControllerActivity?,
    private val serverIp: String,
    private val serverPort: Int
) {
    private var socket: Socket? = null
    private var outputStream: OutputStream? = null

    private var connectTask: ConnectTask? = null

    fun connect() {
        if (connectTask?.status != AsyncTask.Status.RUNNING) {
            connectTask = ConnectTask()
            connectTask?.execute()
        }
    }

    fun sendData(data: ByteArray) {
        val sendDataTask = SendDataTask()
        sendDataTask.execute(data)
    }

    fun disconnect() {
        val disconnectTask = DisconnectTask()
        disconnectTask.execute()
    }

    private fun closeSocket() {
        try {
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        socket = null
    }

    private inner class ConnectTask : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void): Boolean {
            try {
                activity?.showToast("Connecting to ${serverIp}...")
                socket = Socket(serverIp, serverPort)
                outputStream = socket?.getOutputStream()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                activity?.showToast("Connected to server")
            } else {
                activity?.showToast("Failed to connect to server")
            }
        }

        override fun onCancelled() {
            closeSocket()
            outputStream = null
            activity?.showToast("Connection cancelled")
        }
    }

    private inner class SendDataTask : AsyncTask<ByteArray, Void, Boolean>() {
        override fun doInBackground(vararg params: ByteArray): Boolean {
            try {
                val data = params[0]
                outputStream?.write(data)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (!result) {
                activity?.showToast("Failed to send data")
            }
        }
    }

    private inner class DisconnectTask : AsyncTask<Void, Void, Unit>() {
        override fun doInBackground(vararg params: Void) {
            closeSocket()
        }

        override fun onPostExecute(result: Unit) {
            outputStream = null
            activity?.showToast("Disconnected from server")
        }
    }
}