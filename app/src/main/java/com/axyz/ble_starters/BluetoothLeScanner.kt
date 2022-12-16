//package com.axyz.ble_starters
//
//import android.R
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothGatt
//import android.bluetooth.BluetoothGattCallback
//import android.bluetooth.BluetoothGattCharacteristic
//import android.bluetooth.BluetoothGattService
//import android.bluetooth.BluetoothProfile
//import android.content.Context
//import android.widget.ArrayAdapter
//
//class BluetoothLeScanner(private val context: Context) {
//
//    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//    private var bluetoothGatt: BluetoothGatt? = null
//
////    val listAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1)
////    ListView.adapter = listAdapter
//
//    // Scan for BLE devices and connect to the first one found
//    fun scanAndConnect() {
//        if (bluetoothAdapter == null) {
//            // Bluetooth is not supported on this device
//            return
//        }
//
//        if (!bluetoothAdapter.isEnabled) {
//            // Bluetooth is disabled, prompt the user to enable it
//            return
//        }
//
//        // Start scanning for BLE devices
//        bluetoothAdapter.startLeScan { device: BluetoothDevice, rssi: Int, scanRecord: ByteArray ->
//            // Connect to the first BLE device found
//            connect(device)
//            // Stop scanning
//            bluetoothAdapter.stopLeScan(this)
//        }
//    }
//
//    // Connect to a BLE device
//    private fun connect(device: BluetoothDevice) {
//        bluetoothGatt = device.connectGatt(context, false, gattCallback)
//    }
//
//    // GATT callback for handling connection events
//    private val gattCallback = object : BluetoothGattCallback() {
//        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                // Connected to a BLE device, discover its services
//                gatt.discoverServices()
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                // Disconnected from the BLE device, close the GATT instance
//                gatt.close()
//            }
//        }
//
//        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                // Services have been discovered, iterate through them
//                for (service in gatt.services) {
//                    // Read the characteristics of each service
//                    readCharacteristics(service)
//                }
//            }
//        }
//
//        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                // Characteristic has been read, do something with the data
//                val data = characteristic.value
//                // ...
//            }
//        }
//    }
//
//    // Read the characteristics of a BLE service
//    private fun readCharacteristics(service: BluetoothGattService) {
//        for (characteristic in service.characteristics) {
//            // Read each characteristic
//            bluetoothGatt?.readCharacteristic(characteristic)
//        }
//    }
//
//    private fun scan() {
//        if (bluetoothAdapter == null) {
//            // Bluetooth is not supported on this device
//            return
//        }
//
//        if (!bluetoothAdapter.isEnabled) {
//            // Bluetooth is disabled, prompt the user to enable it
//            return
//        }
//
//        // Start scanning for BLE devices
//        bluetoothAdapter.startLeScan { device: BluetoothDevice, rssi: Int, scanRecord: ByteArray ->
//            // Add the BLE device to the list
//            listAdapter.add(device.name ?: device.address)
//        }
//    }
//}
