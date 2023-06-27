package com.rectangleequals.untangled.view

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rectangleequals.untangled.controller.BackgroundService
import com.rectangleequals.untangled.R
import com.rectangleequals.untangled.model.SharedData
import java.net.URI
import java.net.URISyntaxException
import java.util.*

private const val TAG: String = "ControllerActivity"
private const val overlayPermissionRequestCode = 100

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter.isEnabled

    val urlText: String
        get() = urlEditText.text.toString()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlEditText: EditText
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button
    private lateinit var findControllersButton: Button
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var serviceStoppedReceiver: BroadcastReceiver

    private var bluetoothGatt: BluetoothGatt? = null
    private var controller: BluetoothDevice? = null
    private var cachedUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)
        Log.v(TAG, "onCreate")

        enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ControllerActivity", Context.MODE_PRIVATE)

        // Retrieve the cached URL value
        cachedUrl = sharedPreferences.getString("cachedUrl", "") ?: ""

        urlEditText = findViewById(R.id.urlEditText)
        urlEditText.setText(cachedUrl)

        urlEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val url = s.toString().trim()
                if(isValidUrl(url)) {
                    cachedUrl = urlEditText.text.toString()
                    // Store the cached URL value in SharedPreferences
                    sharedPreferences.edit().putString("cachedUrl", cachedUrl).apply()
                    startServiceButton.isEnabled = isValidUrl(url)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No implementation needed
            }
        })

        startServiceButton = findViewById(R.id.startServiceButton)
        startServiceButton.isEnabled = urlText?.let { isValidUrl(it) } == true
        stopServiceButton = findViewById(R.id.stopServiceButton)
        findControllersButton = findViewById(R.id.findControllersButton)

        startServiceButton.setOnClickListener{ beginService() }
        stopServiceButton.setOnClickListener{ endService() }
        findControllersButton.setOnClickListener{ connectToController() }

        SharedData.activityContext = this

        serviceStoppedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                stopServiceButton.isEnabled = false
                startServiceButton.isEnabled = true
                showToast("Service stopped")
            }
        }
        val intentFilter = IntentFilter(BackgroundService.ACTION_SERVICE_STOPPED)
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStoppedReceiver, intentFilter)

        connectToController()
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Overlay permission not granted, show a dialog
            val dialogBuilder = AlertDialog.Builder(this)
                .setTitle("Overlay Permission Required")
                .setMessage("This app requires overlay permission to function properly. Please grant the permission.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    // Open the overlay permission settings
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivityForResult(intent, overlayPermissionRequestCode)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    // User clicked Cancel, handle the case if needed
                }
            dialogBuilder.create().show()
        } else {
            // Overlay permission already granted, proceed with the necessary action
            startBackgroundService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == overlayPermissionRequestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Permission granted, restart the background service or perform the necessary action
                startBackgroundService()
            } else {
                // Permission denied, show a message or handle the case accordingly
                showToast("Overlay permission denied")
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        try {
            val uri = URI(url)
            val port = uri.port
            return port != -1 // Check if a valid port is present
        } catch (e: URISyntaxException) {
            return false
        }
    }

    fun beginService() {
        requestOverlayPermission()
    }

    private fun startBackgroundService() {
        val serviceIntent = Intent(this, BackgroundService::class.java)
        SharedData.activityContext = this
        startServiceButton.isEnabled = false
        stopServiceButton.isEnabled = true
        startService(serviceIntent)
    }

    fun endService() {
        val stopServiceIntent = Intent(this, BackgroundService::class.java)
        stopServiceButton.isEnabled = false
        startServiceButton.isEnabled = true
        stopService(stopServiceIntent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            return true // Don't let the OS handle gamepad inputs
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStoppedReceiver)
        if(hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        }
        super.onDestroy()
    }

    private fun getGameControllerIds(): List<Int> {
        val gameControllerDeviceIds = mutableListOf<Int>()
        val deviceIds = InputDevice.getDeviceIds()
        deviceIds.forEach { deviceId ->
            InputDevice.getDevice(deviceId).apply {
                // Verify that the device has gamepad buttons, control sticks, or both.
                if (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
                    || sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
                    // This device is a game controller. Store its device ID.
                    gameControllerDeviceIds
                        .takeIf { !it.contains(deviceId) }
                        ?.add(deviceId)
                }
            }
        }

        return gameControllerDeviceIds
    }

    private fun connectToController() {
        ensureBluetoothPermission()
        showToast("Finding controller...")
        if(hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            val controllerDevice: BluetoothDevice? =
                pairedDevices?.find { it.name == "Xbox Wireless Controller" }

            if (controllerDevice != null) {
                showToast("Found: ${controllerDevice.name}")
                controller = controllerDevice
                bluetoothGatt = controllerDevice.connectGatt(this, false, gattCallback)
            } else {
                showToast("No controller found")
            }
        } else {
            showToast("Insufficient permissions")
        }
    }



    private fun hasPermission(permission: String): Boolean {
        return applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun ensureBluetoothPermission() {
        showToast("Checking permissions...")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }

    private val gattCallback = @SuppressLint("MissingPermission") object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.v(TAG, "onConnectionStateChange")
            super.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.v(TAG, "onServicesDiscovered")
            super.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            Log.v(TAG, "onCharacteristicRead")
            super.onCharacteristicRead(gatt, characteristic, value, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.v(TAG, "onCharacteristicChanged")
            super.onCharacteristicChanged(gatt, characteristic)
        }
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}