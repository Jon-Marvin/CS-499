package com.example.projecttwo_jm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.view.View

class SMSPermissionActivity : AppCompatActivity() {
    private val smsPermissionCode = 100 // Permission request code for SMS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sms_screen) // Sets the layout for the SMS permission screen

        // Finds the "Grant Permission" button and sets up a click listener
        findViewById<Button>(R.id.btnGrantPermission).setOnClickListener { view ->
            // Checks if the SMS permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                // Requests SMS permission if not already granted
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), smsPermissionCode)
            } else {
                showSnackbar(view, "Permission already granted!") // Shows confirmation message
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val rootView = findViewById<View>(android.R.id.content) // Gets root view for Snackbar display
        if (requestCode == smsPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackbar(rootView, "SMS Permission Granted") // Success message
            } else {
                showSnackbar(rootView, "SMS Permission Denied") // Error message
            }
        }
    }

    // Displays a styled Snackbar message
    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Sets dark blue background
            .setTextColor(Color.WHITE) // Sets white text
            .show()
    }
}
