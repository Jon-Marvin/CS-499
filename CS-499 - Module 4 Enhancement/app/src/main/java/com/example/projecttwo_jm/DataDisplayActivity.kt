package com.example.projecttwo_jm

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color

class DataDisplayActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper // Database helper instance
    private lateinit var recyclerView: RecyclerView // RecyclerView to display inventory items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_screen) // Sets the layout for this activity

        dbHelper = DatabaseHelper(this) // Initializes the database helper

        recyclerView = findViewById(R.id.recyclerView) // Finds RecyclerView in layout
        recyclerView.layoutManager = LinearLayoutManager(this) // Sets layout manager

        val addDataButton: Button = findViewById(R.id.btnAddData) // Finds "Add Data" button
        addDataButton.setOnClickListener {
            showAddDataDialog() // Opens dialog to add new item
        }

        // Retrieves inventory items and sets adapter for RecyclerView
        val items = dbHelper.getAllInventoryItems()
        recyclerView.adapter = InventoryAdapter(items, dbHelper)
    }

    private fun showAddDataDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Item") // Dialog title

        // Create input fields for item name and quantity
        val inputLayout = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val itemNameInput: EditText = inputLayout.findViewById(R.id.etItemName)
        val itemQuantityInput: EditText = inputLayout.findViewById(R.id.etItemQuantity)

        builder.setView(inputLayout) // Sets input layout inside dialog

        builder.setPositiveButton("Add") { _, _ ->
            val itemName = itemNameInput.text.toString()
            val itemQuantity = itemQuantityInput.text.toString().toIntOrNull()

            if (itemName.isNotBlank() && itemQuantity != null) {
                if (dbHelper.addInventoryItem(itemName, itemQuantity)) {
                    showSnackbar("Item added successfully!") // Success message
                    refreshInventoryList() // Refresh the RecyclerView
                } else {
                    showSnackbar("Failed to add item!") // Error message
                }
            } else {
                showSnackbar("Please provide valid input!") // Validation error message
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() } // Cancel button
        builder.show() // Displays the dialog
    }

    // Displays a Snackbar message
    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Sets dark blue background
            .setTextColor(Color.WHITE) // Sets white text
            .show()
    }

    // Refreshes inventory list in RecyclerView after changes
    private fun refreshInventoryList() {
        val items = dbHelper.getAllInventoryItems()
        recyclerView.adapter = InventoryAdapter(items, dbHelper)
        recyclerView.adapter?.notifyDataSetChanged() // Notifies adapter of dataset changes
    }
}
