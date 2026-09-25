package com.example.taller2universidades

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class University(val id: Long, val name: String, val website: String)

class UniversidadDatabase(context: Context) : SQLiteOpenHelper(context, "notasdocente.db", null, 1) {
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE universidades (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, www TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS universidades")
        onCreate(database)
    }

    fun nextId(): Long {
        readableDatabase.rawQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM universidades", null).use { cursor ->
            cursor.moveToFirst()
            return cursor.getLong(0)
        }
    }

    fun save(university: University) {
        val values = ContentValues().apply {
            put("id", university.id)
            put("nombre", university.name)
            put("www", university.website)
        }
        writableDatabase.insertWithOnConflict(
            "universidades",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun find(id: Long): University? {
        readableDatabase.query(
            "universidades",
            arrayOf("id", "nombre", "www"),
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return University(cursor.getLong(0), cursor.getString(1), cursor.getString(2))
        }
    }

    fun delete(id: Long): Boolean {
        return writableDatabase.delete("universidades", "id = ?", arrayOf(id.toString())) > 0
    }

    fun all(): List<University> {
        val universities = mutableListOf<University>()
        readableDatabase.query(
            "universidades",
            arrayOf("id", "nombre", "www"),
            null,
            null,
            null,
            null,
            "id ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                universities += University(cursor.getLong(0), cursor.getString(1), cursor.getString(2))
            }
        }
        return universities
    }
}