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
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_screen)

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addDataButton: Button = findViewById(R.id.btnAddData)
        addDataButton.setOnClickListener {
            showAddDataDialog()
        }

        val items = dbHelper.getAllInventoryItems()
        recyclerView.adapter = InventoryAdapter(items, dbHelper)
    }

    private fun showAddDataDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Item")

        // Create input fields for item name and quantity
        val inputLayout = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val itemNameInput: EditText = inputLayout.findViewById(R.id.etItemName)
        val itemQuantityInput: EditText = inputLayout.findViewById(R.id.etItemQuantity)

        builder.setView(inputLayout)

        builder.setPositiveButton("Add") { _, _ ->
            val itemName = itemNameInput.text.toString()
            val itemQuantity = itemQuantityInput.text.toString().toIntOrNull()

            if (itemName.isNotBlank() && itemQuantity != null) {
                if (dbHelper.addInventoryItem(itemName, itemQuantity)) {
                    showSnackbar("Item added successfully!")
                    refreshInventoryList() // Refresh the RecyclerView
                } else {
                    showSnackbar("Failed to add item!")
                }
            } else {
                showSnackbar("Please provide valid input!")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // Pop-up message for adding an item to the list
    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050"))
            .setTextColor(Color.WHITE) // White text
            .show()
    }

    private fun refreshInventoryList() {
        val items = dbHelper.getAllInventoryItems()
        recyclerView.adapter = InventoryAdapter(items, dbHelper)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}