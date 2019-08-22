package com.oracle.regicidecommon.base

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase

actual fun getSqlDriver(databaseName: String): SqlDriver = NativeSqliteDriver(OacDatabase.Schema, databaseName)
actual fun getPrefsDriver(databaseName: String): SqlDriver = NativeSqliteDriver(PreferencesDatabase.Schema, databaseName)