package com.oracle.regicidecommon.base

import com.squareup.sqldelight.db.SqlDriver

actual fun getSqlDriver(databaseName: String): SqlDriver {
    throw UninitializedPropertyAccessException("This should not be called from the core module, since AndroidSqlDriver requires an Android Context object.")
}
actual fun getPrefsDriver(databaseName: String): SqlDriver {
    throw UninitializedPropertyAccessException("This should not be called from the core module, since AndroidSqlDriver requires an Android Context object.")
}