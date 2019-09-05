package com.oracle.regicidecommon.oac.data

import com.oracle.dv.datasets.OacDatabase
import com.oracle.regicidecommon.models.User

class UserDao(oacDatabase: OacDatabase) {
    private val TAG = "OacDao"

    private val db = oacDatabase.oacQueries

    fun insertUser(host: String, username: String, password: String) {
        db.transaction {
            db.insertUser(host, username, password, false)
        }
    }

    fun insertDefaultUser(user: User) {
        db.transaction {
            db.resetUsers()
            db.insertUser(user.host, user.username, user.password, true)
        }
    }

    fun setDefaultUser(user: User) {
        db.transaction {
            db.resetUsers()
            db.insertUser(user.host, user.username, user.password, true)
        }
    }

    fun getUsersFromHost(host: String): List<User> {
        return try {
            db.getUsersFromHost("%$host%").executeAsList().map {
                User(it.host, it.username, it.password)
            }
        } catch (e: Throwable) {
            com.oracle.regicidecommon.base.error(TAG, "Error getting users from host")
            emptyList()
        }
    }

    fun getDefaultUser(): User? {
        return try {
            db.getDefaultUser().executeAsOne().let {
                User(it.host, it.username, it.password)
            }
        } catch (e: Throwable) {
            com.oracle.regicidecommon.base.error(TAG, "Error getting default user")
            null
        }
    }

    fun clearUsers() {
        db.clearUsers()
    }
}