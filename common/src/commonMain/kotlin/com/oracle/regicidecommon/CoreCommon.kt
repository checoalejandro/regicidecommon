package com.oracle.regicidecommon

import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase
import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.freeze
import com.oracle.regicidecommon.oac.OACRepository
import com.oracle.regicidecommon.oac.data.OACDao
import com.oracle.regicidecommon.prefs.PrefsDao
import com.oracle.regicidecommon.prefs.PrefsManager
import com.squareup.sqldelight.db.SqlDriver
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.kodein.di.erased.with

class CoreCommon(sleeper: Sleeper, oacDriver: SqlDriver, prefsDriver: SqlDriver) {
    init {

    }

    val kodein = Kodein {
        constant("entryUrl") with "http://slc11aso.us.oracle.com:9080/"
        constant("userAuth") with "YWRtaW46d2VsY29tZTE="
        bind() from singleton { sleeper }
        bind() from singleton { OacDatabase(oacDriver) }
        bind() from singleton { PreferencesDatabase(prefsDriver) }
        bind() from singleton { OACDao(instance()) }
        bind() from singleton { PrefsDao(instance()) }
        bind() from singleton { OACApi(instance("entryUrl"), instance("userAuth")) }
        bind() from singleton { PrefsManager(instance()) }
        bind() from singleton { OACRepository(instance(), instance()) }
    }
}

var isInitialized = false
    private set
lateinit var coreCommon: CoreCommon
    private set

fun initializeCoreCommon(sleeper: Sleeper, oacDriver: SqlDriver, prefsDriver: SqlDriver) {
    if (!isInitialized) {
        freeze(sleeper)
        coreCommon = CoreCommon(sleeper, oacDriver, prefsDriver)
        isInitialized = true
    }
}