package com.oracle.regicidecommon.models

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
data class DataSet(
    val name: String,
    val namespace: String,
    val description: String,
    val type: String
)

@Serializable
class DataSetList : MutableList<DataSet> by mutableListOf()

@Serializable
data class Host(val host: String, val port: Int, val ssl: Boolean) {

    fun toUrlKtor(): Url {
        return URLBuilder(toUrl()).build()
    }

    fun toUrl(): String {
        val protocol = if (ssl) "https://" else "http://"
        return "$protocol$host:$port/"
    }
}

@Serializable
data class User(val host: String, val username: String, val password: String)