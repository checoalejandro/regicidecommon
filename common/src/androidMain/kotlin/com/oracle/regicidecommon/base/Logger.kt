package com.oracle.regicidecommon.base

import android.util.Log

actual fun debug(tag: String?, text: String) {
    Log.d(tag, text)
}

actual fun error(tag: String?, text: String) {
    Log.e(tag, text)
}

actual fun warn(tag: String?, text: String) {
    Log.w(tag, text)
}

actual fun info(tag: String?, text: String) {
    Log.i(tag, text)
}