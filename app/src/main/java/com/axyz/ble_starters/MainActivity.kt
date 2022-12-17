package com.axyz.ble_starters

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import com.github.hotchemi.permissionsdispatcher.PermissionRequest
//import com.permissionx.guolindev.PermissionX
import permissions.dispatcher.*


import android.os.ParcelUuid
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.util.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.nio.charset.StandardCharsets
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
//    private var bluetoothService: BluetoothService? = null
//    private var bluetoothService: BluetoothService? = BluetoothService(this,this)


    class DiscoveredBluetoothDevice(val device: BluetoothDevice, val serviceUuids: List<ParcelUuid>)

    val deviceSet = mutableListOf<DiscoveredBluetoothDevice>()
    lateinit var adapter:DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add the Recycler View to keep the list Updated on the screen
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = DeviceAdapter(deviceSet)
        recyclerView.adapter = adapter



        var checkButton = findViewById<Button>(R.id.start_button)
        checkButton.setOnClickListener {
            scan()

        }

        requestPermissions(*permissions)

        val startBroadcastingButton = findViewById<Button>(R.id.start_broadcasting_button)
        startBroadcastingButton.setOnClickListener {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            // Check if Bluetooth is supported on the device
            if (bluetoothAdapter == null) {
                Log.e("Bluetooth", "Bluetooth is not supported on this device")
                return@setOnClickListener
            }

            // Check if Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled()) {
                Log.i("Bluetooth", "Bluetooth is not enabled, enabling now...")
                bluetoothAdapter.enable()
            }

//             Get the BluetoothLeAdvertiser
            val bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

            // Check if Bluetooth LE advertising is supported on the device
            if (bluetoothLeAdvertiser == null) {
                Log.e("Bluetooth", "Bluetooth LE advertising is not supported on this device")
                return@setOnClickListener
            }
            startBluetoothServiceTry(bluetoothLeAdvertiser)

        }

    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_view)
    }

    class DeviceAdapter(private val devices: MutableList<DiscoveredBluetoothDevice>) : RecyclerView.Adapter<DeviceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
            return DeviceViewHolder(view)
        }

        override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
            val descoveredDevice = devices.elementAt(position)
            holder.textView.text = "${descoveredDevice.device.name ?: descoveredDevice.device.address} -- ${descoveredDevice.serviceUuids}"
            holder.textView.setOnClickListener(){
                println("Item on click listner")
            }
        }

        override fun getItemCount(): Int {
            return devices.size
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

        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        val scanSettings = ScanSettings.Builder().build()
        val scanFilter = ScanFilter.Builder().build()
        // List to store the scan results
        val scanResultList: MutableList<ScanResult> = mutableListOf()

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                // Add the scan result to the list
                scanResultList.add(result)

                // Get the ScanRecord object from the ScanResult
                val scanRecord: ScanRecord? = result.getScanRecord()
//                val scanRecord: ScanRecord = ...

                val device: BluetoothDevice = result.device
                // Check if the device is already in the list
//                if (deviceSet.contains(device)) {
                if (deviceSet.any { it.device == device }) {
                    // Device is already in the list, do nothing
                } else {
                    // Print the service UUIDs
//                val serviceUuids: List<ParcelUuid> = scanRecord?.getServiceUuids() as List<ParcelUuid>
                    val serviceUuids: List<ParcelUuid> = scanRecord?.getServiceUuids() ?: emptyList()
                    if (serviceUuids.isNotEmpty()) {
                        println("Service UUIDs: $serviceUuids")
                    } else {
                        println("No service UUIDs available")
                    }
                    // Device is new, add it to the list
                    deviceSet.add(DiscoveredBluetoothDevice(device, serviceUuids))
                    println("Device Added ------------> $device")

// Print the device name
                    val deviceName: String? = scanRecord?.getDeviceName()
                    if (deviceName != null) {
                        println("Device name: $deviceName")
                    } else {
                        println("Device name not available")
                    }



                    // Print the manufacturer data
                    val manufacturerData: SparseArray<ByteArray>? = scanRecord?.getManufacturerSpecificData()
                    if (manufacturerData != null) {
                        if (manufacturerData.isNotEmpty()) {
                            for (i in 0 until manufacturerData.size()) {
                                val manufacturerId = manufacturerData.keyAt(i)
                                val data = manufacturerData.valueAt(i)
                                println("Manufacturer ID: $manufacturerId, Data: ${String(data, StandardCharsets.UTF_8)}")
                            }
                        } else {
                            println("No manufacturer data available")
                        }
                    }

// Print the service data
                    val serviceData: Map<ParcelUuid, ByteArray>? = scanRecord?.getServiceData()
                    if (serviceData != null) {
                        println("Service data: $serviceData")
                    } else {
                        println("Service data not available")
                    }

// Print the Tx power level
                    val txPowerLevel: Int? = scanRecord?.getTxPowerLevel()
                    if (txPowerLevel != null) {
                        println("Tx power level: $txPowerLevel")
                    } else {
                        println("Tx power level not available")
                    }

//// Print the scan response data
//                    val scanResponse: ByteArray? = scanRecord?.get
//                    if (scanResponse != null) {
//                        println("Scan response data: ${Hex.encodeHexString(scanResponse)}")
//                    } else {
//                        println("Scan response data not available")
//                    }

                    adapter.notifyDataSetChanged()
                }
            }
        }


        bluetoothLeScanner.startScan(listOf(scanFilter), scanSettings, scanCallback)

        // To detect that a device is no longer available, you can periodically check the list of detected
        // devices and remove any devices that are no longer detected in the scan
//        val handler = Handler()
//        val runnable = object : Runnable {
//            override fun run() {
//                // Iterate over the list of detected devices
////                println("Handler Running"+deviceSet)
//                for (device in deviceSet) {
//                    // Check if the device is still being detected in the scan
//                    if (!scanResultList.any { it.device == device }) {
//                        // Device is no longer being detected, remove it from the list
//                        deviceSet.remove(device)
//                        adapter.notifyDataSetChanged()
//                        println("Device Removed -> "+device)
//                    }
//                }
//
//                // Repeat the check after a certain interval
//                handler.postDelayed(this, 1000)
//            }
//        }
//
//        // Start the periodic check
//        handler.post(runnable)

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

                Toast.makeText(this@MainActivity,"Forward to Screen saying permissions are required",Toast.LENGTH_SHORT).show()
                // Create an Intent to start the Permission Required activity
                val intent = Intent(this, PermissionRequired::class.java)

                // Start the Permission required activity
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

