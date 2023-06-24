package com.rectangleequals.untangled

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// Notification constants
private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "BackgroundServiceChannel"
private const val CHANNEL_NAME = "Background Service"

class BackgroundService : Service() {
    private var controllerActivity: ControllerActivity? = null
    private var tcpClient: TcpClient? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        SharedData.backgroundService = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Retrieve the tcpClient from SharedData
        controllerActivity = SharedData.activityContext
        tcpClient = controllerActivity?.tcpClient
        controllerActivity?.connectToServer()

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

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        controllerActivity?.disconnectFromServer()
        sendServiceStoppedBroadcast()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_STOP_SERVICE = "com.rectangleequals.untangled.STOP_SERVICE"
        const val ACTION_SERVICE_STOPPED = "com.rectangleequals.untangled.SERVICE_STOPPED"
    }
}
