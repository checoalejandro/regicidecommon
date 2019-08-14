package com.oracle.regicidecommon.base

interface WeakRef<T: Any> {
    val value: T?
}

expect fun <T: Any> buildWeakRef(value: T): WeakRef<T>