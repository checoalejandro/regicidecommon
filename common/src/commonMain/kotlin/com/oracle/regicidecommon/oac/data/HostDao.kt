package com.oracle.regicidecommon.oac.data

import com.oracle.dv.datasets.OacDatabase
import com.oracle.regicidecommon.models.Host

class HostDao(database: OacDatabase) {
    private val TAG = "OacDao"

    private val db = database.oacQueries

    fun insertHost(host: String, port: Int, ssl: Boolean) {
        db.transaction {
            db.insertHost(host, port.toLong(), ssl)
        }
    }

    fun getHosts(): List<Host>? {
        return try {
            db.getHosts().executeAsList().map {
                Host(it.host, it.port.toInt(), it.ssl)
            }
        } catch (e: Throwable) {
            null
        }
    }

    fun getHosts(host: String): List<Host>? {
        return try {
            db.getHost(host).executeAsList().map {
                Host(it.host, it.port.toInt(), it.ssl)
            }
        } catch (e: Throwable) {
            null
        }
    }

    fun clearHosts() {
        db.clearHosts()
    }

    fun deleteHosts(host: String) {
        db.deleteHost(host)
    }
}