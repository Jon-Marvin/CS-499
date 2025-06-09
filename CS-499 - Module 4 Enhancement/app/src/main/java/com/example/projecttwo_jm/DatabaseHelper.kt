package com.example.projecttwo_jm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database properties
        const val DATABASE_NAME = "InventoryApp.db"
        const val DATABASE_VERSION = 1

        // User table columns
        const val TABLE_USERS = "Users"
        const val COLUMN_USERNAME = "Username"
        const val COLUMN_PASSWORD = "Password"

        // Inventory table columns
        const val TABLE_INVENTORY = "Inventory"
        const val COLUMN_ITEM_NAME = "ItemName"
        const val COLUMN_ITEM_QUANTITY = "ItemQuantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users table
        db?.execSQL("CREATE TABLE $TABLE_USERS ($COLUMN_USERNAME TEXT PRIMARY KEY, $COLUMN_PASSWORD TEXT)")

        // Create Inventory table
        db?.execSQL("CREATE TABLE $TABLE_INVENTORY ($COLUMN_ITEM_NAME TEXT PRIMARY KEY, $COLUMN_ITEM_QUANTITY INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Delete old tables and recreate them on upgrade
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        onCreate(db)
    }

    // Adds a new user to the database
    fun addUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L // Returns true if insert is successful
    }

    // Validates login credentials by checking database records
    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val isValid = cursor.count > 0 // User exists if count > 0
        cursor.close()
        return isValid
    }

    // Adds an item to the inventory database
    fun addInventoryItem(itemName: String, quantity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, itemName)
        values.put(COLUMN_ITEM_QUANTITY, quantity)
        val result = db.insert(TABLE_INVENTORY, null, values)
        return result != -1L // Returns true if insert is successful
    }

    // Retrieves all inventory items from the database
    fun getAllInventoryItems(): MutableList<Pair<String, Int>> {
        val db = readableDatabase
        val cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, null)
        val items = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val itemName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME))
            val itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
            items.add(Pair(itemName, itemQuantity)) // Adds item to the list
        }
        cursor.close()
        return items // Returns list of inventory items
    }

    // Deletes an inventory item based on item name
    fun deleteInventoryItem(itemName: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_INVENTORY, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName))
        return result > 0 // Returns true if delete is successful
    }

    // Checks if an inventory item exists before updating
    fun itemExists(itemName: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_INVENTORY, arrayOf(COLUMN_ITEM_NAME), "$COLUMN_ITEM_NAME = ?", arrayOf(itemName), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists // Returns true if the item exists
    }

    // Updates the quantity of an existing inventory item
    fun updateInventoryItem(itemName: String, quantity: Int): Boolean {
        if (!itemExists(itemName)) {
            Log.d("DatabaseDebug", "Item does not exist: $itemName")
            return false // Prevents updating non-existent items
        }

        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_QUANTITY, quantity)
        val result = db.update(TABLE_INVENTORY, values, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName))
        Log.d("DatabaseDebug", "Update result: $result rows affected")
        return result > 0 // Returns true if update is successful
    }
}
