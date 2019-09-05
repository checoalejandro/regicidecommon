package com.oracle.regicidecommon.oac.viewmodels

import com.oracle.regicidecommon.base.*
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.oac.data.OACRepository
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.kodein.di.erased.instance

class OACListViewModel: BaseViewModel<OACCoordinator, DatasetListState>(), DatasetListActions {

    val oacRepository: OACRepository by coreCommon.kodein.instance()

    init {
        debug(TAG, "Init")

        launch {
            oacRepository.subscribeDatasets()
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
        launch { oacRepository.fetchDatasets() }
    }

    override fun onDatasetClicked(namespace: String, name: String) {
        coordinator?.value?.showDataset(namespace, name)
    }

    override fun fetchDataSetListFromDb() {
        debug(TAG, "Fetching dataSet list from db")
        launch { oacRepository.fetchDataSetsFromDb() }
    }
}

data class DatasetListState(val datasetList: List<DataSet>): State {
    override var s: String = "dataSetList"
}

interface DatasetListActions: Actions {
    fun fetchDatasetList()
    fun onDatasetClicked(namespace: String, name: String)
    fun fetchDataSetListFromDb()
}

interface OACCoordinator: Coordinator {
    fun showDataset(namespace: String, name: String)
}