package com.oracle.regicide.androidcommon

import android.content.Context
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase
import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.initializeCoreCommon
import com.oracle.regicidecommon.oac.viewmodels.OACListViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OacListTest {

    lateinit var context: Context
    lateinit var oacListViewModel: OACListViewModel

    @BeforeClass
    fun setup() {
        initializeCoreCommon(object : Sleeper {
            override fun sleepThread(delay: Long) {
                Thread.sleep(delay)
            }

        }, AndroidSqliteDriver(OacDatabase.Schema, context, "oac.db"),
            AndroidSqliteDriver(PreferencesDatabase.Schema, context, "prefs.db"))
        oacListViewModel = OACListViewModel()
    }

    @Test
    fun sample() {
        runBlocking {
            launch {
                val result = oacListViewModel.fetchDatasetList()
                org.junit.Assert.assertNotNull(result)
            }
        }
    }

}