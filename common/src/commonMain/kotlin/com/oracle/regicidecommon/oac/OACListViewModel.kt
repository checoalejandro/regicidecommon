package com.oracle.regicidecommon.oac

import com.oracle.regicidecommon.OACApi
import com.oracle.regicidecommon.base.Actions
import com.oracle.regicidecommon.base.BaseViewModel
import com.oracle.regicidecommon.base.Coordinator
import com.oracle.regicidecommon.base.State
import com.oracle.regicidecommon.models.DataSet

class OACListViewModel: BaseViewModel<OACCoordinator, OACState>(), OACActions {

    private val oacRepo: OACRepository = OACRepository(OACApi("", ""))

    override fun getInitialState(): OACState {
        return OACState(emptyList())
    }

    override fun fetchDatasetList() {

    }

    override fun onDatasetClicked(namespace: String, name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

data class OACState(val datasetList: List<DataSet>): State

interface OACActions: Actions {
    fun fetchDatasetList()
    fun onDatasetClicked(namespace: String, name: String)
}

interface OACCoordinator: Coordinator {
    fun showDataset(namespace: String, name: String)
}