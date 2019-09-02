package com.oracle.regicide.androidcommon

import android.content.Context
import android.service.autofill.Dataset
import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase
import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.StateChangeListener
import com.oracle.regicidecommon.base.debug
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.initializeCoreCommon
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.oac.data.OACApi
import com.oracle.regicidecommon.oac.data.OACDao
import com.oracle.regicidecommon.oac.data.OACRepository
import com.oracle.regicidecommon.oac.viewmodels.DatasetListState
import com.oracle.regicidecommon.oac.viewmodels.OACListViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.mockito.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.powermock.reflect.Whitebox
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLog

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(RobolectricTestRunner::class)
class OacTest {

    lateinit var kodein: Kodein
    lateinit var oacApi: OACApi
    lateinit var oacDao: OACDao
    lateinit var oacRepository: OACRepository

    lateinit var context: Context
    lateinit var viewModel: OACListViewModel

    @Before
    fun setup() {
        ShadowLog.stream = System.out
        context = RuntimeEnvironment.systemContext
        initializeCoreCommon(
            object : Sleeper {
                override fun sleepThread(delay: Long) {
                    Thread.sleep(delay)
                }
            }, AndroidSqliteDriver(OacDatabase.Schema, context, "oac.db"),
            AndroidSqliteDriver(PreferencesDatabase.Schema, context, "prefs.db")
        )
        oacDao = mock(OACDao::class.java)
        oacApi = mock(OACApi::class.java)
        oacRepository = OACRepository(oacApi, oacDao)

        kodein = Kodein {
            extend(coreCommon.kodein, true)
            bind(overrides = true) from singleton { oacDao }
            bind(overrides = true) from singleton { oacApi }
            bind(overrides = true) from singleton { oacRepository }
        }
        Whitebox.setInternalState(coreCommon, Kodein::class.java, kodein)

        viewModel = OACListViewModel()
    }

    @Test
    fun `simple item request`() {
        runBlocking {
            launch {
                `when`(oacApi.getDatasets()).thenReturn(listOf(DataSet("customName", "customName", "customName", "customName")))
                viewModel.setStateChangeListener(object : StateChangeListener<DatasetListState> {
                    override fun onStateChange(state: DatasetListState) {
                        if (state.datasetList.isEmpty()) return
                        assertEquals(1, state.datasetList.count())
                        assertEquals("customName", state.datasetList.first().name)
                    }
                })
                viewModel.fetchDatasetList()
            }
        }
    }

    @Test
    fun `persistent data test`() {
        val secondListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertEquals(1, state.datasetList.count())
            }
        }
        val firstListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertEquals(1, state.datasetList.count())
                viewModel.setStateChangeListener(secondListener)
                runBlocking {
                    launch {
                        viewModel.fetchDatasetList()
                    }
                }
            }
        }

        runBlocking {
            launch {
                `when`(oacApi.getDatasets()).thenReturn(
                    listOf(DataSet("customName", "customName", "customName", "customName")),
                    null)
                viewModel.setStateChangeListener(firstListener)
                viewModel.fetchDatasetList()
            }
        }
    }

    @Test
    fun `update item from db`() {
        val thirdListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertEquals(1, state.datasetList.count())
                assertEquals("NewValue", state.datasetList.first().type)
            }

        }
        val secondListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertEquals(1, state.datasetList.count())
                viewModel.setStateChangeListener(thirdListener)
                runBlocking {
                    launch {
                        viewModel.fetchDatasetList()
                    }
                }
            }
        }
        val firstListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertNotNull(state)
                assertEquals(1, state.datasetList.count())
                viewModel.setStateChangeListener(secondListener)
                runBlocking {
                    launch {
                        viewModel.fetchDatasetList()
                    }
                }
            }
        }

        runBlocking {
            launch {
                `when`(oacApi.getDatasets())
                    .thenReturn(listOf(DataSet("NewValue", "NewValue", "customName", "customName")))
                    .thenReturn(mutableListOf(DataSet("NewValue", "NewValue", "NewValue", "NewValue")))
                    .thenReturn(null)
                viewModel.setStateChangeListener(firstListener)
                viewModel.fetchDatasetList()
            }
        }
    }

    @Test
    fun `clear items from db`() {
        val secondListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                assertEquals(0, state.datasetList.count())
            }
        }
        val firstListener = object : StateChangeListener<DatasetListState> {
            override fun onStateChange(state: DatasetListState) {
                if (state.datasetList.isEmpty()) return
                assertNotNull(state)
                assertEquals(1, state.datasetList.count())
                viewModel.setStateChangeListener(secondListener)
                runBlocking {
                    launch {
                        viewModel.fetchDatasetList()
                    }
                }
            }
        }

        runBlocking {
            launch {
                `when`(oacApi.getDatasets())
                    .thenReturn(listOf(DataSet("NewValue", "NewValue", "customName", "customName")))
                    .thenReturn(emptyList())
                viewModel.setStateChangeListener(firstListener)
                viewModel.fetchDatasetList()
            }
        }
    }

    @Test
    fun `mockito test`() {
        runBlocking {
            launch {
                `when`(oacApi.getDatasets())
                .thenReturn(null)
                .thenReturn(listOf(DataSet("NewValue", "NewValue", "customName", "customName")))
                .thenReturn(mutableListOf(DataSet("NewValue", "NewValue", "NewValue", "NewValue")))
                .thenReturn(null)

                println(oacApi.getDatasets())
                println(oacApi.getDatasets())
                println(oacApi.getDatasets())
                println(oacApi.getDatasets())
            }
        }
    }

}