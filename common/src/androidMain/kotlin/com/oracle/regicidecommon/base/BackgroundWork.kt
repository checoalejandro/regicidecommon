package com.oracle.regicidecommon.base

import java.util.concurrent.Executors

val executor = Executors.newCachedThreadPool()

actual fun dispatchBackgroundWork(block: () -> Unit) {
    executor.submit(object : Thread() {
        override fun run() {
            block.invoke()
        }
    })
}