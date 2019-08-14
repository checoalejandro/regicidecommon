package com.oracle.regicidecommon.base

import kotlin.native.ref.WeakReference

class iOSWeakRef<T: Any>(val weakValue: WeakReference<T>): WeakRef<T> {
    override val value: T?
        get() = weakValue.get()
}

actual fun <T: Any> buildWeakRef(value: T): WeakRef<T> {
    return iOSWeakRef(WeakReference(value))
}