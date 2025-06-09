package com.example.projecttwo_jm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.view.View

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_screen)

        dbHelper = DatabaseHelper(this)

        val usernameField: EditText = findViewById(R.id.setUsername)
        val passwordField: EditText = findViewById(R.id.setPassword)
        val confirmButton: Button = findViewById(R.id.btnConfirmCreateAccount)

        confirmButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (validateInput(username, password)) {
                if (dbHelper.addUser(username, password)) {
                    showSnackbar(confirmButton, "Account created successfully!")
                    finish() // Close this screen after successful account creation
                } else {
                    showSnackbar(confirmButton, "User already exists!")
                }
            } else {
                showSnackbar(confirmButton, "Username and password cannot be blank!")
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        return username.isNotBlank() && password.isNotBlank()
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Dark blue background
            .setTextColor(Color.WHITE) // White text
            .show()
    }
}
