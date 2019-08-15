package com.oracle.regicidecommon.oac

import com.oracle.regicidecommon.base.*
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.generateBackgroundWork
import com.oracle.regicidecommon.models.DataSet
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.kodein.di.erased.instance

class OACListViewModel: BaseViewModel<OACCoordinator, DatasetListState>(), DatasetListActions {

    private val oacRepo: OACRepository by coreCommon.kodein.instance()
    private val sleeper: Sleeper by coreCommon.kodein.instance()

    init {
        debug(TAG, "Init")
        dispatchBackgroundWork { generateBackgroundWork(sleeper) }

        launch {
            oacRepo.subscribeDatasets()
                .consumeEach { list ->
                    stateChannel.mutate { it.copy(datasetList = list) }
                }
        }
    }

    override fun getInitialState(): DatasetListState {
        return DatasetListState(emptyList())
    }

    override fun fetchDatasetList() {
        debug(TAG, "Fetching dataset list")
        stateChannel.mutate { it.copy() }
        launch { oacRepo.fetchDatasets() }
    }

    override fun onDatasetClicked(namespace: String, name: String) {
        coordinator?.value?.showDataset(namespace, name)
    }

}

data class DatasetListState(val datasetList: List<DataSet>): State

interface DatasetListActions: Actions {
    fun fetchDatasetList()
    fun onDatasetClicked(namespace: String, name: String)
}

interface OACCoordinator: Coordinator {
    fun showDataset(namespace: String, name: String)
}