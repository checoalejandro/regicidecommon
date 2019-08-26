package com.oracle.regicidecommon.oac.data

import com.oracle.regicidecommon.models.DataSet
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
    private val textClient = HttpClient()
    private val client = HttpClient {
        engine {
            threadsCount = 4
            pipelining = true
        }
        useDefaultTransformers = true
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict).apply {
                setMapper(DataSet::class, DataSet.serializer())
            }
        }
    }

    suspend fun getDataset(
        namespace: String,
        name: String,
        success: (DataSet) -> Unit,
        failure: (Throwable?) -> Unit
    ) {
        try {
            val json = client.get<String> {
                apiUrl("api/datasetsvc/public/api/v4/datasets/'$namespace'.'$name'", userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer(), json)
                .also(success)
        } catch (e: Throwable) {
            failure(e)
        }
    }

    suspend fun getDatasets(): List<DataSet>? {
        return try {
            val json = client.get<String> {
                apiUrl("api/datasetsvc/public/api/v4/datasets", userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer().list, json)
        } catch (t: Throwable) {
            com.oracle.regicidecommon.base.error("OACApi", t.message ?: "Error fetching datasets")
            null
        }
    }

    suspend fun getDatasets(success: (List<DataSet>) -> Unit, failure: (Throwable?) -> Unit) {
        try {
            val json = client.get<String> {
                apiUrl("api/datasetsvc/public/api/v4/datasets", userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer().list, json)
                .also(success)
        } catch (t: Throwable) {
            failure(t)
        }
    }

    suspend fun getDataSetCanonicalData(
        namespace: String,
        name: String,
        success: (List<List<String>>) -> Unit,
        failure: (Throwable?) -> Unit
    ) {
        try {
            val path = "api/datasetsvc/public/api/v4/datasets/'$namespace'.'$name'/canonical-data"
            val canonicalData = textClient.get<String> {
                apiUrl(
                    path,
                    userAuth
                )
            }
            canonicalData.split("\n")
                .map { it.split(",") }
                .toList()
                .also(success)
        } catch (e: Throwable) {
            failure(e)
        }
    }

    private fun HttpRequestBuilder.apiUrl(path: String, userId: String) {
        header(HttpHeaders.Authorization, "Basic $userId")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}