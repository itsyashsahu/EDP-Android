package com.axyz.ble_starters

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.runWithPermissions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import com.github.hotchemi.permissionsdispatcher.PermissionRequest
//import com.permissionx.guolindev.PermissionX
import permissions.dispatcher.*
import com.axyz.ble_starters.BluetoothService


import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import android.widget.*
import java.util.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )
    var askedForPermssions = false

    lateinit var listAdapter:ArrayAdapter<String>
//    var bleDevices = mutableSetOf<String>("Really a Device")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Find the ListView object in the layout file
        val listView = findViewById<ListView>(R.id.list_view)
        // Create the list adapter
        listAdapter = ArrayAdapter<String>(this, R.layout.list_item, R.id.text_view)

//         Set the list adapter as the adapter for the ListView
        listView.adapter = listAdapter
        listAdapter.add("Heelasd")

//         Set an OnItemClickListener for the ListView
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Do something when an item is clicked
        }

//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bleDevices.toList())
        val adapter = ArrayAdapter<String>(this, R.id.list_view, R.id.textView)

//        val deviceListView
        listView.adapter = adapter

        var checkButton = findViewById<Button>(R.id.start_button)
        checkButton.setOnClickListener {
//            showDialougeIfPermissionsNotGiven()
            listAdapter.add("new Element")
//            bleDevices.add("Naya Device")
            adapter.notifyDataSetChanged()
//            listView.deferNotifyDataSetChanged()
//            adapter.notifyDataSetChanged()
//            listView.adapter=adapter


            println("After Adding Naya Device $listAdapter")
        }

        requestPermissions(*permissions)

        val startBroadcastingButton = findViewById<Button>(R.id.start_broadcasting_button)
        startBroadcastingButton.setOnClickListener {
//            startBluetoothService()
//            startBroadCasting()
            // Get the default Bluetooth adapter
//            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//
//            // Check if Bluetooth is supported on the device
//            if (bluetoothAdapter == null) {
//                Log.e("Bluetooth", "Bluetooth is not supported on this device")
//                return@setOnClickListener
//            }
//
//            // Check if Bluetooth is enabled
//            if (!bluetoothAdapter.isEnabled()) {
//                Log.i("Bluetooth", "Bluetooth is not enabled, enabling now...")
//                bluetoothAdapter.enable()
//            }

            // Get the BluetoothLeAdvertiser
//            val bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
//
//            // Check if Bluetooth LE advertising is supported on the device
//            if (bluetoothLeAdvertiser == null) {
//                Log.e("Bluetooth", "Bluetooth LE advertising is not supported on this device")
//                return@setOnClickListener
//            }
//            startBluetoothServiceTry(bluetoothLeAdvertiser)
            scan()
        }

    }


    private fun scan() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            Log.e("Bluetooth", "Bluetooth is not supported on this device")
            return
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            Log.i("Bluetooth", "Bluetooth is not enabled, enabling now...")
            bluetoothAdapter.enable()
        }
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            // Bluetooth is disabled, prompt the user to enable it
            return
        }

        // Start scanning for BLE devices
//        bluetoothAdapter.startDiscovery()
        bluetoothAdapter.startLeScan { device: BluetoothDevice, rssi: Int, scanRecord: ByteArray ->
            // Add the BLE device to the list
            println("Device -> "+device)
            listAdapter.add(device.name ?: device.address)
//            bleDevices.add(device.toString())
        }
    }

    //Modifinng the Raw Advertisement data
    private fun startBluetoothService() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val REQUEST_ENABLE_BT = 1
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val serviceIntent = Intent(this, HelloService::class.java)
        startService(serviceIntent)
    }


//    Below two functions help to advertise a custom characteristic and service with their own uuid
    private fun startBluetoothServiceTry(bluetoothLeAdvertiser: BluetoothLeAdvertiser){

        // Create a characteristic with some data
        val characteristic = BluetoothGattCharacteristic(
            UUID.fromString("00000000-7770-1000-8000-00805f9b34fb"),
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

// Convert the string to a byte array using UTF-8 encoding
        val data = "Hello, world!".toByteArray(Charsets.UTF_8)
        println("Hello worled "+data)
// Set the value of the characteristic to the byte array
        characteristic.value = data

// Generate a unique identifier for the characteristic based on its data
        val hash = UUID.nameUUIDFromBytes(data).hashCode()

// Add the characteristic to a database hash using the generated identifier
        val database = HashMap<Int, BluetoothGattCharacteristic>()
        database[hash] = characteristic

        // Create a descriptor with the data
        val descriptor = BluetoothGattDescriptor(
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"),
            BluetoothGattDescriptor.PERMISSION_READ
        )
        descriptor.value = "Hello, world!".toByteArray(Charsets.UTF_8)

        // Add the descriptor to the characteristic
        characteristic.addDescriptor(descriptor)

        // Create a service that contains the characteristic
        val service = BluetoothGattService(
            UUID.fromString("00007609-0000-1000-8000-00805f9b34fb"),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        service.addCharacteristic(characteristic)

        // Create an AdvertiseData object and include the service UUID
        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(service.uuid))
            .build()

        // Create an AdvertiseSettings object and set the advertising mode
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .build()

        // Start advertising the service
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, advertiseCallback)

    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.i("Bluetooth", "Advertising started successfully")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("Bluetooth", "Advertising failed with error code: $errorCode")
        }
    }



    fun requestPermissions(vararg permissions: String) {
        // Request the necessary permissions
        println("Asking For Permissions")
        Dexter.withActivity(this)
            .withPermissions(*permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report != null) {
                        println("Report -> "+report.deniedPermissionResponses)
                    }
                    // Check if all permissions are granted
                    if (report?.areAllPermissionsGranted() == true) {

                        Log.d("Permission Status : ","All permissions are granted")
                        // All permissions are granted, proceed with the app
                        Toast.makeText(this@MainActivity,"All permissions are granted",Toast.LENGTH_SHORT).show()
                    } else {
//                        showMessageDialog("Some Permission are Denied")
                        Log.d("Permission Status : ","Some permissions are denied")
                        // Some permissions are denied, show a message to the user
                    }
                    askedForPermssions = true;
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
//                    askedForPermssions = true
                }
            })
            .onSameThread()
            .check()


    }

    fun checkPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun showMessageDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setNegativeButton("Cancel") { _, _ ->
                // Do nothing
                Toast.makeText(this@MainActivity,"Forward to Screen saying permissions are required",Toast.LENGTH_SHORT).show()
                // Create an Intent to start the second activity
                val intent = Intent(this, PermissionRequired::class.java)

// Start the new activity
                startActivity(intent)


            }
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .show()
    }

    fun showDialougeIfPermissionsNotGiven(){
        if(!checkPermissions(*permissions)){
            showMessageDialog("Aree Kar do Baba")
            Log.d("Permission Status Check: ","Some Permissions are Denied")
        }else{
            Log.d("Permission Status Check: ","All permissions are granted")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("RESUME","On Resume Called")
        if(askedForPermssions){
            showDialougeIfPermissionsNotGiven()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("PAUSE","On Pause Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unbind the BluetoothService from the Activity
//        unbindService(serviceConnection)
    }
}

