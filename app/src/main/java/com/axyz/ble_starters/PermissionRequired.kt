package com.axyz.ble_starters

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PermissionRequired: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permissions_required)

//        var checkButton = findViewById<Button>(R.id.start_button)
//        checkButton.setOnClickListener {
//            showDialougeIfPermissionsNotGiven()
//            checkPermissions(*permissions)
//        }
//        requestPermissions(*permissions)
        println("Succesfull Forwarded to another activity")
    }
}