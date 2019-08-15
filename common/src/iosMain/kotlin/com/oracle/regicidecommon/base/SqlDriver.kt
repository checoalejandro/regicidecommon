package com.oracle.regicidecommon.base

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import com.oracle.dv.datasets.OacDatabase

actual fun getSqlDriver(databaseName: String): SqlDriver = NativeSqliteDriver(OacDatabase.Schema, databaseName)