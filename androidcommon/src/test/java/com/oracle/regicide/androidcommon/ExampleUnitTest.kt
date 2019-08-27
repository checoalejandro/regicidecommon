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
class ExampleUnitTest {

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

        kodein = Kodein {
            extend(coreCommon.kodein, true)
            bind(overrides = true) from singleton { oacDao }
            bind(overrides = true) from singleton { oacApi }
            bind(overrides = true) from singleton { OACRepository(oacApi, oacDao) }
        }
        Whitebox.setInternalState(coreCommon, Kodein::class.java, kodein)

        viewModel = OACListViewModel()
    }

    @Test
    fun sampleRequest() {
        runBlocking {
            launch {
                `when`(oacApi.getDatasets()).thenReturn(listOf(DataSet("alex", "alex", "alex", "alex")))
                viewModel.setStateChangeListener(object : StateChangeListener<DatasetListState> {
                    override fun onStateChange(state: DatasetListState) {
                        if (state.datasetList.isEmpty()) return
                        assertNotNull(state)
                        assertEquals(1, state.datasetList.count())
                    }
                })
                viewModel.fetchDatasetList()
            }
        }

    }
}