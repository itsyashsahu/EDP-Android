package com.axyz.ble_starters

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.StandardCharsets
import java.util.*

class DiscoveredDevice : AppCompatActivity(){
//    val services: Array<BluetoothGattService> =
//        intent.getParcelableArrayExtra("services") as Array<BluetoothGattService>
//    val device: BluetoothDevice? = intent.getParcelableExtra("device")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discovered_device)

        val deviceNameText :TextView = findViewById(R.id.device_string)

//        deviceNameText.text = "$deviceNameText ${device?.name ?: device?.address}"

        println("From The Discoverd Device File --------")
//        for (service in services) {
//            println("Service UUID: ${service.uuid}")
//            val characteristics = service.characteristics
//            for (characteristic in characteristics) {
//                println("Characteristic UUID: ${characteristic.uuid}")
//                if( characteristic.value == null ){
//                    println("Characteristics Null Value : ")
//                }else{
//                    println("Characteristic data: ${String(characteristic.value, StandardCharsets.UTF_8)}")
//                }
//            }
//        }


    }
}