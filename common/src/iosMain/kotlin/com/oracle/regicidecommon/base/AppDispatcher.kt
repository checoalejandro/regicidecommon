package com.oracle.regicidecommon.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.darwin.*
import kotlin.coroutines.CoroutineContext

// Currently, switching to a different thread is broken to due to how multi-threading works on Kotlin Native
// (https://github.com/Kotlin/kotlinx.coroutines/issues/462)
//internal actual val IODispatcher: CoroutineDispatcher = NsQueueDispatcher(
//    dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0)
//)

internal actual val MainDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())

internal class NsQueueDispatcher(private val dispatchQueue: dispatch_queue_t) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}