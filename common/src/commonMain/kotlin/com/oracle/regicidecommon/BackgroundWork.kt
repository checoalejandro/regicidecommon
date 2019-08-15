package com.oracle.regicidecommon

import com.oracle.regicidecommon.base.Sleeper
import com.oracle.regicidecommon.base.debug

fun generateBackgroundWork(sleeper: Sleeper): () -> Unit = {
    debug("generateBackgroundWork", "Starting work")
    sleeper.sleepThread(5000)
    debug("generateBackgroundWork", "Still working...")
    sleeper.sleepThread(5000)
    debug("generateBackgroundWork", "Finishing work")
}