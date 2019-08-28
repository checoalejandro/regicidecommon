package com.oracle.regicidecommon.base

import io.ktor.http.encodeURLPath

actual fun encodeString(string: String) = string.encodeURLPath()