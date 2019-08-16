package com.oracle.regicidecommon.oac.data

import com.oracle.dv.datasets.OacDatabase
import com.oracle.regicidecommon.models.DataSet

class OACDao(database: OacDatabase) {

    private val db = database.oacQueries

    fun insertDataset(dataSet: DataSet) {
        db.transaction {
            db.insertDataset(dataSet.name, dataSet.namespace, dataSet.description, dataSet.type)
        }
    }

    fun selectDataset(name: String): DataSet? {
        try {
            db.selectDataset(name).executeAsOne().let {
                return DataSet(
                    it.name,
                    it.namespace ?: "",
                    it.description ?: "",
                    it.type ?: ""
                )
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun selectDatasets(): List<DataSet>? {
        return try {
            db.selectDatasets().executeAsList().map {
                DataSet(
                    it.name,
                    it.namespace ?: "",
                    it.description ?: "",
                    it.type ?: ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun deleteDataset(name: String) = db.deleteDataset(name)

    fun deleteAll() = db.deleteAll()
}