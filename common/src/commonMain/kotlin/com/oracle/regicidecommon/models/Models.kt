package com.oracle.regicidecommon.models

import kotlinx.serialization.Serializable

@Serializable
data class DataSet(
        val name: String,
        val namespace: String,
        val description: String,
        val type: String)

@Serializable
class DataSetList: MutableList<DataSet> by mutableListOf()