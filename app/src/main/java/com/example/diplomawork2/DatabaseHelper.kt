package com.example.diplomawork2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 2  // увеличили версию до 2
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_RECORD = "record"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_RECORD INTEGER DEFAULT 0);")  // добавили колонку record
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Добавляем колонку record, если её нет
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_RECORD INTEGER DEFAULT 0")
        }
        // Если в будущем будут новые версии, добавляйте дополнительные условия
    }

    fun insertUser(username: String, password: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_RECORD, 0)  // при создании пользователя рекорд 0
        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun getRecord(username: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_RECORD),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        val record = if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORD)) else 0
        cursor.close()
        return record
    }

    fun updateRecord(username: String, newRecord: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECORD, newRecord)
        }
        db.update(TABLE_NAME, values, "$COLUMN_USERNAME = ?", arrayOf(username))
    }
}
