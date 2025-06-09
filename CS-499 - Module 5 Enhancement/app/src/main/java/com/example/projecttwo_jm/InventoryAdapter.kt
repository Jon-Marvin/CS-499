package com.example.projecttwo_jm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color

class InventoryAdapter(
    private val inventoryItems: MutableList<Pair<String, Int>>, // List of inventory items
    private val dbHelper: DatabaseHelper // Database helper instance
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val updateButton: Button = itemView.findViewById(R.id.btnUpdateItem) // Update button
        val itemName: TextView = itemView.findViewById(R.id.tvItemName) // Item name
        val itemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity) // Item quantity
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteItem) // Delete button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        // Inflates the layout for each inventory item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_item, parent, false)
        return InventoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inventoryItems.size // Returns the total number of items
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        // Retrieves item details and sets values in UI elements
        val (name, quantity) = inventoryItems[position]
        holder.itemName.text = name
        holder.itemQuantity.text = quantity.toString()

        // Handle update button click
        holder.updateButton.setOnClickListener {
            showUpdateDialog(holder.itemView, name, quantity, position) // Opens update dialog
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            if (dbHelper.deleteInventoryItem(name)) {
                showSnackbar(holder.itemView, "Item deleted successfully!") // Success message
                inventoryItems.removeAt(position) // Removes item from list
                notifyItemRemoved(position) // Updates RecyclerView display
            } else {
                showSnackbar(holder.itemView, "Failed to delete item!") // Error message
            }
        }
    }

    private fun showUpdateDialog(view: View, itemName: String, currentQuantity: Int, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Update Item Quantity") // Dialog title

        val inputLayout = LayoutInflater.from(view.context).inflate(R.layout.dialog_update_item, null)
        val quantityInput: EditText = inputLayout.findViewById(R.id.etNewQuantity)
        quantityInput.setText(currentQuantity.toString()) // Pre-fill current quantity

        builder.setView(inputLayout) // Sets input layout inside dialog
        builder.setPositiveButton("Update") { _, _ ->
            val newQuantity = quantityInput.text.toString().toIntOrNull()

            if (newQuantity != null) {
                if (dbHelper.updateInventoryItem(itemName, newQuantity)) {
                    showSnackbar(view, "Item updated successfully!") // Success message
                    inventoryItems[position] = Pair(itemName, newQuantity) // Updates UI
                    notifyItemChanged(position) // Refreshes RecyclerView
                } else {
                    showSnackbar(view, "Failed to update item!") // Error message
                }
            } else {
                showSnackbar(view, "Invalid quantity!") // Validation error message
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() } // Cancel button
        builder.show() // Displays the dialog
    }

    // Displays a styled Snackbar message
    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Sets dark blue background
            .setTextColor(Color.WHITE) // Sets white text
            .show()
    }
}
