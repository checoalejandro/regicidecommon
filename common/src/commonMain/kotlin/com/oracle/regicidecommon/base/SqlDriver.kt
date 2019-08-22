package com.oracle.regicidecommon.base

import com.squareup.sqldelight.db.SqlDriver

expect fun getSqlDriver(databaseName: String): SqlDriver
expect fun getPrefsDriver(databaseName: String): SqlDriver