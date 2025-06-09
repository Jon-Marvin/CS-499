package com.example.projecttwo_jm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color

class InventoryAdapter(
    private val inventoryItems: List<Pair<String, Int>>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tvItemName)
        val itemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_item, parent, false)
        return InventoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return inventoryItems.size
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val (name, quantity) = inventoryItems[position]
        holder.itemName.text = name
        holder.itemQuantity.text = quantity.toString()

        holder.deleteButton.setOnClickListener {
            if (dbHelper.deleteInventoryItem(name)) {
                showSnackbar(holder.itemView, "Item deleted successfully!")
                (inventoryItems as MutableList).removeAt(position)
                notifyItemRemoved(position)
            } else {
                showSnackbar(holder.itemView, "Failed to delete item!")
            }
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#000050")) // Dark blue background
            .setTextColor(Color.WHITE) // White text
            .show()
    }
}