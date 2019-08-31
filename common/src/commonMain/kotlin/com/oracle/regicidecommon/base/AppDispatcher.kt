package com.oracle.regicidecommon.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal expect val MainDispatcher: CoroutineDispatcher
internal expect val heavyDispatcher: CoroutineDispatcher
internal expect val parsingDispatcher: CoroutineDispatcher
internal expect val dbDispatcher: CoroutineDispatcher