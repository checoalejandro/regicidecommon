package com.oracle.regicidecommon.oac.data

import com.oracle.regicidecommon.base.encodeString
import com.oracle.regicidecommon.models.DataSet
import com.oracle.regicidecommon.utils.RequestPaths.OAC_GET_CANONICAL_DATA
import com.oracle.regicidecommon.utils.RequestPaths.OAC_GET_DATASET
import com.oracle.regicidecommon.utils.RequestPaths.OAC_GET_DATASETS
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.util.pipeline.pipelineExecutorFor
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

class OACApi(val endPoint: String, val userAuth: String) {
    private val TAG = "OACApi"
    private val textClient = HttpClient()
    private val client = HttpClient {
        engine {
            threadsCount = 4
            pipelining = true
        }
        useDefaultTransformers = true
        install(JsonFeature)
    }

    suspend fun getDataset(
        namespace: String,
        name: String
    ): DataSet? {
        return try {
            val json = client.get<String> {
                apiUrl(encodeString("$OAC_GET_DATASET'$namespace'.'$name'"), userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer(), json)
        } catch (e: Throwable) {
            com.oracle.regicidecommon.base.error(TAG, "Error getting dataset ${e.message}")
            null
        }
    }

    suspend fun getDatasets(): List<DataSet>? {
        return try {
            val json = client.get<String> {
                apiUrl(OAC_GET_DATASETS, userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer().list, json)
        } catch (t: Throwable) {
            com.oracle.regicidecommon.base.error("OACApi", t.message ?: "Error fetching datasets")
            null
        }
    }

    suspend fun getDataSetCanonicalData(
        namespace: String,
        name: String
    ): List<List<String>>? {
        return try {
            val path = encodeString("$OAC_GET_CANONICAL_DATA'$namespace'.'$name'/canonical-data")
            val canonicalData = textClient.get<String> {
                apiUrl(
                    path,
                    userAuth
                )
            }
            canonicalData.split("\n")
                .map { it.split(",") }
                .toList()
        } catch (e: Throwable) {
            com.oracle.regicidecommon.base.error(TAG, "Error getting canonical data ${e.message}")
            null
        }
    }

    private fun HttpRequestBuilder.apiUrl(path: String, userId: String) {
        header(HttpHeaders.Authorization, "Basic $userId")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }

    private suspend fun requestString(path: String): String {
        return client.get<String> {
            apiUrl("api/datasetsvc/public/api/v4/datasets", userAuth)
        }
    }
}