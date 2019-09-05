package com.oracle.regicidecommon.oac.viewmodels

import com.oracle.regicidecommon.base.Actions
import com.oracle.regicidecommon.base.BaseViewModel
import com.oracle.regicidecommon.base.Coordinator
import com.oracle.regicidecommon.base.State
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.oac.data.OACRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.kodein.di.erased.instance

@ExperimentalCoroutinesApi
class DataSetDetailViewModel(val namespace: String, val name: String) :
    BaseViewModel<Coordinator, DataSetDetailState>(),
    DataSetDetailActions, Coordinator {

    private val oacRepository: OACRepository by coreCommon.kodein.instance()

    init {
        launch {
            oacRepository.fetchDataset(namespace, name)
                .consumeEach { dataSet ->
                    stateChannel.mutate { it.copy(dataSet = dataSet) }
                }
        }
    }

    override fun getInitialState() = DataSetDetailState(null, null)

    override fun fetchCanonicalData(namespace: String, name: String) {
        launch {
            oacRepository.fetchCanonicalData(namespace, name)
                .consumeEach { canonicalData ->
                    stateChannel.mutate { it.copy(canonicalData = canonicalData) }
                }
        }
    }

    override fun returnInitialState(): Boolean {
        return false
    }

}

data class DataSetDetailState(
    val dataSet: DataSet?,
    val canonicalData: List<List<String>>?
) : State {
    override var s: String = "dataSet"
}

interface DataSetDetailActions : Actions {
    fun fetchCanonicalData(namespace: String, name: String)
}