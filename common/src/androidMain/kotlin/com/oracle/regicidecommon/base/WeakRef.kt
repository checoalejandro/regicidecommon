package com.oracle.regicidecommon.base

import java.lang.ref.WeakReference

class AndroidWeakRef<T: Any>(val weakValue: WeakReference<T>) : WeakRef<T> {
    override val value: T?
        get() = weakValue.get()
}

actual fun <T: Any> buildWeakRef(value: T): WeakRef<T> {
    return AndroidWeakRef(WeakReference(value))
}