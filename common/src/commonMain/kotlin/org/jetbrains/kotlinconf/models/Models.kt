package org.jetbrains.kotlinconf.models

import kotlinx.serialization.Serializable

@Serializable
data class DataSet(
        val name: String,
        val namespace: String,
        val description: String,
        val type: String)

@Serializable
class EmptyList: MutableList<DataSet> by mutableListOf()