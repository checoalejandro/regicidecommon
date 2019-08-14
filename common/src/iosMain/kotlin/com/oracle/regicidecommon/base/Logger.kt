package com.oracle.regicidecommon.base

import platform.Foundation.NSLog

actual fun debug(tag: String?, text: String) {
    NSLog("DEBUG($tag): $text")
}

actual fun error(tag: String?, text: String) {
    NSLog("ERROR($tag): $text")
}

actual fun warn(tag: String?, text: String) {
    NSLog("WARN($tag): $text")
}

actual fun info(tag: String?, text: String) {
    NSLog("INFO($tag): $text")
}