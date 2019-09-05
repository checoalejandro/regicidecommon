package com.oracle.regicidecommon.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

internal actual val MainDispatcher: CoroutineDispatcher = Dispatchers.Main
internal actual val heavyDispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
internal actual val parsingDispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
internal actual val dbDispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()