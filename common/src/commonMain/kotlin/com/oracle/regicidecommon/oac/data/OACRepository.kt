package com.oracle.regicidecommon.oac

import com.oracle.regicidecommon.OACApi
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.oac.data.OACDao
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select

class OACRepository(private val oacApi: OACApi,
                    private val oacDao: OACDao) {

    private val oacChannel = ConflatedBroadcastChannel<List<DataSet>>()

    fun subscribeDatasets() = oacChannel.openSubscription()

    suspend fun suscribeDataSet(namespace: String, name: String) : ReceiveChannel<DataSet> {
        val channel = ConflatedBroadcastChannel<DataSet>()
        oacApi.getDataset(namespace, name, success = {
            oacDao.insertDataset(it)
            channel.offer(it)
        }, failure = {
            com.oracle.regicidecommon.base.error("OACRepository", "Error fetching $name dataset")
        })
        return channel.openSubscription()
    }

    suspend fun fetchDatasets() {
        oacApi.getDatasets(success = {
            oacChannel.offer(it)
        }, failure = {
            com.oracle.regicidecommon.base.error("OACRepository", "Error fetching dataset list")
        })
    }

}