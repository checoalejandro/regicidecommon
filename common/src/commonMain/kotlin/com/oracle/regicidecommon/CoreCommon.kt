package com.oracle.regicidecommon

import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.freeze
import com.oracle.regicidecommon.oac.OACRepository
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import org.kodein.di.erased.with

class CoreCommon(sleeper: Sleeper) {
    init {

    }

    val kodein = Kodein {
        constant("entryUrl") with "http://slc11aso.us.oracle.com:9080/"
        constant("userAuth") with "YWRtaW46d2VsY29tZTE="
        bind() from singleton { sleeper }
        bind() from singleton { OACApi(instance("entryUrl"), instance("userAuth")) }
        bind() from singleton { OACRepository(instance()) }
    }
}

var isInitialized = false
    private set
lateinit var coreCommon: CoreCommon
    private set

fun initializeCoreCommon(sleeper: Sleeper) {
    if (!isInitialized) {
        freeze(sleeper)
        coreCommon = CoreCommon(sleeper)
        isInitialized = true
    }
}