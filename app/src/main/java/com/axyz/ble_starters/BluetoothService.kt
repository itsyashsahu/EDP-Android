package com.axyz.ble_starters
//package com.axyz.bluetooth

import android.Manifest
import android.app.Activity
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class BluetoothService(private val mainActivity: MainActivity,private val context: Context) : Service() {


    // A constant for the request code used when requesting Bluetooth permissions
    private companion object {
        const val REQUEST_BLUETOOTH_PERMISSIONS = 1
    }

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val serverSocket: BluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
        "BluetoothService",
        UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    )

    // A reference to the thread that runs the server
    private var serverThread: Thread? = null

    inner class LocalBinder : Binder() {
        // Return the BluetoothService instance
        fun getService() = this@BluetoothService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): LocalBinder {
        // Return the LocalBinder instance
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {
        // Check if the app has the necessary Bluetooth permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the Bluetooth permissions
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
        } else {
            // Start the Bluetooth server
            print("Lets Start the Bluetooth Server")
            startBluetoothServer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startBluetoothServer() {
        // Create a new thread to run the server
        serverThread = object : Thread() {
            override fun run() {
                var shouldListen = true
                while (shouldListen) {
                    try {
                        // Listen for incoming connections
                        val socket = serverSocket.accept()
                        if (socket != null) {
                            // A device has connected to the server
                            Log.d("BluetoothService", "A device has connected to the server")

                            // Get the OutputStream for the socket
                            val outputStream = socket.outputStream

                            // Write a message to the OutputStream
                            val writer = OutputStreamWriter(outputStream)
                            writer.write("Hello from the Bluetooth server!\n")
                            writer.flush()

                            // Close the socket
                            socket.close()
                        }
                    } catch (e: IOException) {
                        // An error occurred while listening for incoming connections
                        Log.e("BluetoothService", "Error listening for incoming connections", e)
                        shouldListen = false
                    }
                }
            }
        }

        // Start the server thread
        serverThread?.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop the server thread
        serverThread?.interrupt()

// Close the server socket
        serverSocket?.close()
    }

}