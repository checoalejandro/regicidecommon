package com.oracle.regicidecommon

import com.oracle.dv.datasets.OacDatabase
import com.oracle.dv.preferences.PreferencesDatabase
import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.freeze
import com.oracle.regicidecommon.base.getPrefsDriver
import com.oracle.regicidecommon.base.getSqlDriver
import com.oracle.regicidecommon.login.data.LoginApi
import com.oracle.regicidecommon.login.repositories.LoginRepository
import com.oracle.regicidecommon.oac.data.OACApi
import com.oracle.regicidecommon.oac.data.OACRepository
import com.oracle.regicidecommon.oac.data.OACDao
import com.oracle.regicidecommon.prefs.PrefsDao
import com.oracle.regicidecommon.prefs.PrefsManager
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.features.cookies.ConstantCookiesStorage
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.kodein.di.erased.with
import kotlin.reflect.KClass

class CoreCommon(val sleeper: Sleeper, val oacDriver: SqlDriver, val prefsDriver: SqlDriver) {

    init {

    }

    var kodein = initKodein("http://slc11aso.us.oracle.com:9080/", "YWRtaW46d2VsY29tZTE=")

    fun initKodein(entryPoint: String, userAuth: String = "YWRtaW46d2VsY29tZTE=") = Kodein {
        constant("entryUrl") with entryPoint
        constant("userAuth") with userAuth

        bind() from singleton { sleeper }

        // Databases
        bind() from singleton { oacDriver }
        bind() from singleton { OacDatabase(oacDriver) }
        bind() from singleton { PreferencesDatabase(prefsDriver) }

        // DAOs
        bind() from singleton { OACDao(instance()) }
        bind() from singleton { PrefsDao(instance()) }

        // APIs
        bind() from singleton { OACApi(instance("entryUrl"), instance("userAuth")) }
        bind() from singleton { LoginApi(instance("entryUrl")) }

        // Other
        bind() from singleton { PrefsManager(instance()) }
        bind() from singleton { ConstantCookiesStorage() }

        // Repositories
        bind() from singleton { OACRepository(instance(), instance()) }
        bind() from singleton { LoginRepository(instance()) }
    }
}

private var version = "0.0.6"

var isInitialized = false
    private set
lateinit var coreCommon: CoreCommon
    private set

fun getCurrentVersion() = version

fun reinitialize(entryPoint: String, userAuth: String) {
    coreCommon.initKodein(entryPoint, userAuth)
}

fun initializeCoreCommon(sleeper: Sleeper, oac: SqlDriver? = null, prefs: SqlDriver? = null) {
    if (!isInitialized) {
        freeze(sleeper)
        coreCommon = CoreCommon(sleeper, oac ?: getSqlDriver("oac.db"), prefs ?: getPrefsDriver("prefs.db"))
        isInitialized = true
    }
}