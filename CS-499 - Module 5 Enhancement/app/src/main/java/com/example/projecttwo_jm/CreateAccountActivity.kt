package com.example.projecttwo_jm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.view.View

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper // Database helper instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_screen) // Sets the layout for this activity

        dbHelper = DatabaseHelper(this) // Initializes the database helper

        // Finds UI elements in the layout
        val usernameField: EditText = findViewById(R.id.setUsername)
        val passwordField: EditText = findViewById(R.id.setPassword)
        val confirmButton: Button = findViewById(R.id.btnConfirmCreateAccount)

        // Handles account creation when button is clicked
        confirmButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            // Checks if username and password are not blank
            if (validateInput(username, password)) {
                // Attempts to add user to the database
                if (dbHelper.addUser(username, password)) {
                    showSnackbar(confirmButton, "Account created successfully!")
                    finish() // Closes this activity after successful account creation
                } else {
                    showSnackbar(confirmButton, "User already exists!") // User already registered
                }
            } else {
                showSnackbar(confirmButton, "Username and password cannot be blank!") // Prevents empty fields
            }
        }
    }

    // Checks that username and password are not empty
    private fun validateInput(username: String, password: String): Boolean {
        return username.isNotBlank() && password.isNotBlank()
    }

    // Displays a styled Snackbar message
    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Sets dark blue background
            .setTextColor(Color.WHITE) // Sets white text
            .show()
    }
}
