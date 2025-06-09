package com.example.projecttwo_jm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper // Database helper for managing user authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen) // Sets the layout for the login screen

        dbHelper = DatabaseHelper(this) // Initializes the database helper

        // Finds UI elements in the layout
        val usernameField: EditText = findViewById(R.id.setUsername) // Username input field
        val passwordField: EditText = findViewById(R.id.setPassword) // Password input field
        val loginButton: Button = findViewById(R.id.btnLogin) // Login button
        val createAccountButton: Button = findViewById(R.id.btnCreateAccount) // Create account button

        // Handles login button click
        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            // Validates input before checking credentials
            if (validateInput(username, password)) {
                // Checks credentials in the database
                if (dbHelper.validateUser(username, password)) {
                    showSnackbar(loginButton, "Login successful!") // Success message
                    startActivity(Intent(this, DataDisplayActivity::class.java)) // Open data display screen
                } else {
                    showSnackbar(loginButton, "Invalid credentials!") // Error message for incorrect login
                }
            } else {
                showSnackbar(loginButton, "Username and password cannot be blank!") // Prevents empty fields
            }
        }

        // Opens the create account screen when clicked
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    // Checks if username and password are not empty
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
