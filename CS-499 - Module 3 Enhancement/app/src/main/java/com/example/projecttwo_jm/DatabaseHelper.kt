package com.example.projecttwo_jm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "InventoryApp.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "Users"
        const val COLUMN_USERNAME = "Username"
        const val COLUMN_PASSWORD = "Password"

        const val TABLE_INVENTORY = "Inventory"
        const val COLUMN_ITEM_NAME = "ItemName"
        const val COLUMN_ITEM_QUANTITY = "ItemQuantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        db?.execSQL("CREATE TABLE $TABLE_USERS ($COLUMN_USERNAME TEXT PRIMARY KEY, $COLUMN_PASSWORD TEXT)")

        // Create Inventory Table
        db?.execSQL("CREATE TABLE $TABLE_INVENTORY ($COLUMN_ITEM_NAME TEXT PRIMARY KEY, $COLUMN_ITEM_QUANTITY INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        onCreate(db)
    }

    // Add a new user
    fun addUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    // Validate login credentials
    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME, COLUMN_PASSWORD),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    // CRUD operations for inventory
    fun addInventoryItem(itemName: String, quantity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, itemName)
        values.put(COLUMN_ITEM_QUANTITY, quantity)
        val result = db.insert(TABLE_INVENTORY, null, values)
        return result != -1L
    }

    fun getAllInventoryItems(): List<Pair<String, Int>> {
        val db = readableDatabase
        val cursor = db.query(TABLE_INVENTORY, null, null, null, null, null, null)
        val items = mutableListOf<Pair<String, Int>>()
        while (cursor.moveToNext()) {
            val itemName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME))
            val itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
            items.add(Pair(itemName, itemQuantity))
        }
        cursor.close()
        return items
    }

    fun deleteInventoryItem(itemName: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_INVENTORY, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName))
        return result > 0
    }

    fun updateInventoryItem(itemName: String, quantity: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ITEM_QUANTITY, quantity)
        val result = db.update(TABLE_INVENTORY, values, "$COLUMN_ITEM_NAME = ?", arrayOf(itemName))
        return result > 0
    }
}