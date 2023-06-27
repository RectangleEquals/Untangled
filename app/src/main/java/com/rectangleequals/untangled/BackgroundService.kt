package com.rectangleequals.untangled

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.net.URI


private const val TAG = "BackgroundService"

// Notification constants
private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "BackgroundServiceChannel"
private const val CHANNEL_NAME = "Background Service"

class BackgroundService : Service() {
    private var controllerActivity: ControllerActivity? = null
    private var tcpClient: TcpClient? = null
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: CustomOverlayLayout

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        SharedData.backgroundService = this
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null) as CustomOverlayLayout
        overlayView.addGamepadEventListener{ event -> handleGamepadEvent(event) }

        overlayView.setOnTouchListener(null)

        overlayView.visibility = INVISIBLE
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == ACTION_STOP_SERVICE) {
            doStopService()
            return START_NOT_STICKY
        }

        // Retrieve the tcpClient from SharedData
        controllerActivity = SharedData.activityContext as ControllerActivity
        connectToServer()

        val layoutType: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val layoutFlags: Int = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            layoutFlags,
            PixelFormat.TRANSLUCENT
        )
        // Add the overlay view to the window manager
        windowManager.addView(overlayView, layoutParams)

        createNotificationChannel()

        // Create the stop service intent
        val stopServiceIntent = Intent(this, BackgroundService::class.java)
        stopServiceIntent.action = ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(
            this,
            0,
            stopServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Untangled")
            .setContentText("Service Running")
            .setSmallIcon(R.drawable.logo_32)
            .addAction(R.drawable.stop_32, "Stop", pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        overlayView.visibility = VISIBLE

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }

            val manager = SharedData.activityContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            manager?.createNotificationChannel(channel)
        }
    }

    private fun sendServiceStoppedBroadcast() {
        val intent = Intent(ACTION_SERVICE_STOPPED)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun connectToServer() {
        val uri = URI(controllerActivity?.urlText)
        tcpClient = TcpClient(controllerActivity, uri.host, uri.port)
        tcpClient?.connect()
    }

    private fun handleGamepadEvent(event: GamepadInputEvent): Boolean {
        val controllerState = ControllerState(event)
        val serializedState = SerializedControllerState(controllerState).serializedData
        tcpClient?.sendData(serializedState)
        Log.v(TAG, "[GAMEPAD]: $event")
        return true
    }

    private fun disconnectFromServer() {
        tcpClient?.disconnect()
    }

    private fun doStopService() {
        overlayView.visibility = INVISIBLE
        overlayView.removeGamepadEventListener { event -> handleGamepadEvent(event) }
        stopSelf()
        stopForeground(STOP_FOREGROUND_REMOVE)
        disconnectFromServer()
        sendServiceStoppedBroadcast()
    }

    override fun onDestroy() {
        doStopService()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_STOP_SERVICE = "com.rectangleequals.untangled.STOP_SERVICE"
        const val ACTION_SERVICE_STOPPED = "com.rectangleequals.untangled.SERVICE_STOPPED"
    }
}
