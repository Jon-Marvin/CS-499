package com.example.projecttwo_jm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest // SHA-256 for password hashing

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "InventoryApp.db"
        const val DATABASE_VERSION = 1

        // Users table
        const val TABLE_USERS = "Users"
        const val COLUMN_USERNAME = "Username"
        const val COLUMN_PASSWORD = "Password"

        // Inventory table
        const val TABLE_INVENTORY = "Inventory"
        const val COLUMN_ITEM_NAME = "ItemName"
        const val COLUMN_ITEM_QUANTITY = "ItemQuantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create tables for users and inventory
        db?.execSQL("CREATE TABLE $TABLE_USERS ($COLUMN_USERNAME TEXT PRIMARY KEY, $COLUMN_PASSWORD TEXT)")
        db?.execSQL("CREATE TABLE $TABLE_INVENTORY ($COLUMN_ITEM_NAME TEXT PRIMARY KEY, $COLUMN_ITEM_QUANTITY INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Reset database on upgrade (can be improved for migrations)
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        onCreate(db)
    }

    // Hashes passwords using SHA-256
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    // Adds a new user with a hashed password
    fun addUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, hashPassword(password)) // Store hashed password
        }
        return db.insert(TABLE_USERS, null, values) != -1L
    }

    // Validates login by comparing hashed passwords
    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, hashPassword(password)), // Compare hashed versions
            null, null, null
        )
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    // Adds an item to the inventory
    fun addInventoryItem(itemName: String, quantity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, itemName)
            put(COLUMN_ITEM_QUANTITY, quantity)
        }
        return db.insert(TABLE_INVENTORY, null, values) != -1L
    }

    // Retrieves all inventory items as a list of pairs (name, quantity)
    fun getAllInventoryItems(): MutableList<Pair<String, Int>> {
        val db = readableDatabase
        val cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, null)
        val items = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            items.add(Pair(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
            ))
        }
        cursor.close()
        return items
    }

    // Deletes an inventory item by name
    fun deleteInventoryItem(itemName: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_INVENTORY, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName)) > 0
    }

    // Checks if an inventory item exists
    fun itemExists(itemName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_INVENTORY WHERE $COLUMN_ITEM_NAME = ?", arrayOf(itemName))
        val exists = cursor.moveToFirst() && cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    // Updates the quantity of an inventory item if it exists
    fun updateInventoryItem(itemName: String, quantity: Int): Boolean {
        if (!itemExists(itemName)) return false
        val db = writableDatabase
        val values = ContentValues().apply { put(COLUMN_ITEM_QUANTITY, quantity) }
        return db.update(TABLE_INVENTORY, values, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName)) > 0
    }
}
