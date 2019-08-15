package com.oracle.regicidecommon.prefs

import com.oracle.dv.preferences.PreferencesDatabase

class PrefsDao (database: PreferencesDatabase) {

    private val db = database.prefsdbQueries

    fun insertStringPref(key: String, value: String) {
        db.transaction {
            db.insertPref(key, value, value)
        }
    }

    fun insertBoolPref(key: String, value: Boolean) {
        db.transaction {
            db.insertPref(key, value.toString(), value.toString())
        }
    }

    fun insertIntPref(key: String, value: Int) {
        db.transaction {
            db.insertPref(key, value.toString(), value.toString())
        }
    }

    fun deleteAll() {
        db.deleteAll()
    }

    fun getStringPref(key: String, defaultValue: String = ""): String {
        return db.selectPref(key).executeAsOneOrNull()?.string_value ?: defaultValue
    }
    fun getBoolPref(key: String, defaultValue: Boolean) : Boolean {
        return db.selectPref(key).executeAsOneOrNull()?.string_value?.toBoolean() ?: defaultValue
    }
    fun getIntPref(key: String, defaultValue: Int) : Int {
        return db.selectPref(key).executeAsOneOrNull()?.string_value?.toInt() ?: defaultValue
    }
}