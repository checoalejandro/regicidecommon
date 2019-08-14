package com.oracle.regicidecommon.base

import kotlin.native.concurrent.freeze

actual fun freeze(obj: Any) {
    obj.freeze()
}