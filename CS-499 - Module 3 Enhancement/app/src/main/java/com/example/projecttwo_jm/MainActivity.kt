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
    private lateinit var dbHelper: DatabaseHelper // Added database helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        dbHelper = DatabaseHelper(this) // Initialize database helper

        val usernameField: EditText = findViewById(R.id.setUsername)
        val passwordField: EditText = findViewById(R.id.setPassword)
        val loginButton: Button = findViewById(R.id.btnLogin)
        val createAccountButton: Button = findViewById(R.id.btnCreateAccount)

        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (validateInput(username, password)) {
                if (dbHelper.validateUser(username, password)) { // Check credentials in DB
                    showSnackbar(loginButton, "Login successful!")
                    startActivity(Intent(this, DataDisplayActivity::class.java))
                } else {
                    showSnackbar(loginButton, "Invalid credentials!")
                }
            } else {
                showSnackbar(loginButton, "Username and password cannot be blank!")
            }
        }

        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java)) // Opens new screen
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
