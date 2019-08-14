package com.oracle.regicidecommon.base

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

actual fun dispatchBackgroundWork(block: () -> Unit) {
    block.freeze()
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { block }) {
        it.invoke()
    }
}