package com.axyz.ble_starters

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.nio.charset.Charset

class HelloService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        println("OnstartCommand")
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val message = "Hello, everyone!"
        val byteArray = message.toByteArray(Charset.defaultCharset())
        println(byteArray.contentToString())
        val record = AdvertiseData.Builder()
            .addManufacturerData(1, byteArray)
            .build()
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        advertiser?.startAdvertising(settings, record, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.d(TAG, "Advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "Advertising failed with error code: $errorCode")
            }
        })

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
