package com.oracle.regicidecommon.oac.data

import com.oracle.regicidecommon.base.debug
import com.oracle.regicidecommon.base.error
import com.oracle.regicidecommon.models.DataSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

@ExperimentalCoroutinesApi
class OACRepository(
    private val oacApi: OACApi,
    private val oacDao: OACDao
) {

    private val TAG = "OacRepository"

    private val oacChannel = ConflatedBroadcastChannel<List<DataSet>>()

    fun subscribeDatasets() = oacChannel.openSubscription()

    suspend fun fetchDataset(namespace: String, name: String): ReceiveChannel<DataSet> {
        val channel = ConflatedBroadcastChannel<DataSet>()
        val dataset = oacApi.getDataset(namespace, name)
        if (dataset == null) {
            error(TAG, "Error fetching dataset")
            oacDao.selectDataset(name)?.let { channel.offer(it) }
        } else {
            oacDao.insertDataset(dataset)
            channel.offer(dataset)
        }
        return channel.openSubscription()
    }

    suspend fun fetchDataSetsFromDb() {
        oacChannel.offer(oacDao.selectDatasets() ?: emptyList())
    }

    suspend fun fetchDatasets() {
        val list = oacApi.getDatasets()
        if (list == null) {
            error(
                TAG,
                "Error fetching dataset list from network"
            )
            oacChannel.offer(oacDao.selectDatasets() ?: emptyList())
        } else {
            if (list.isEmpty()) {
                debug(TAG, "Successfully fetched empty list... clearing list")
                oacDao.deleteAll()
                oacChannel.offer(list)
            } else {
                debug(TAG, "Successfully fetched... storing in db")
                list.forEach { dataSet -> oacDao.insertDataset(dataSet) }
                oacChannel.offer(list)
            }
        }
    }

    suspend fun fetchCanonicalData(
        namespace: String,
        name: String
    ): ReceiveChannel<List<List<String>>?> {
        val channel = ConflatedBroadcastChannel<List<List<String>>?>()
        val canonicalData = oacApi.getDataSetCanonicalData(namespace, name)
        channel.offer(canonicalData)
        return channel.openSubscription()
    }

}