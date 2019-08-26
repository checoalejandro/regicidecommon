package com.oracle.regicide.androidcommon

import android.content.Context
import android.service.autofill.Dataset
import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase
import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.StateChangeListener
import com.oracle.regicidecommon.initializeCoreCommon
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.oac.data.OACApi
import com.oracle.regicidecommon.oac.data.OACRepository
import com.oracle.regicidecommon.oac.viewmodels.DatasetListState
import com.oracle.regicidecommon.oac.viewmodels.OACListViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.mockito.*
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.powermock.reflect.Whitebox
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    lateinit var oacApi: OACApi
    lateinit var oacRepository: OACRepository

    lateinit var context: Context
    lateinit var viewModel: OACListViewModel

    @Before
    fun setup() {
        context = RuntimeEnvironment.systemContext
        initializeCoreCommon(
            object : Sleeper {
                override fun sleepThread(delay: Long) {
                    Thread.sleep(delay)
                }
            }, AndroidSqliteDriver(OacDatabase.Schema, context, "oac.db"),
            AndroidSqliteDriver(PreferencesDatabase.Schema, context, "prefs.db")
        )
        oacApi = mock(OACApi::class.java)
        oacRepository = mock(OACRepository::class.java)
        viewModel = OACListViewModel()
    }

    @Test
    fun sample() {
        runBlocking {
            launch {
                `when`(oacApi.getDatasets()).thenReturn(listOf(DataSet("alex", "alex", "alex", "alex")))
            }
        }
        Whitebox.setInternalState(oacRepository, OACApi::class.java, oacApi)
        Whitebox.setInternalState(viewModel, OACRepository::class.java, oacRepository)
        runBlocking {
            launch {
                val listener = object : StateChangeListener<DatasetListState> {
                    override fun onStateChange(state: DatasetListState) {
                        println(state.datasetList)
                        assertNotNull(state.datasetList)
                    }

                }
                viewModel.fetchDatasetList()
                viewModel.setStateChangeListener(listener)
            }
        }
    }
}