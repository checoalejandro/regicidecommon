package com.oracle.regicidecommon

import com.oracle.regicidecommon.models.DataSet
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

class OACApi(val endPoint: String, val userAuth: String) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict).apply {
                setMapper(DataSet::class, DataSet.serializer())
            }
        }
    }

    suspend fun getDataset(namespace: String, name: String, success: (DataSet) -> Unit, failure: (Throwable?) -> Unit) {
        try {
            val json = client.get<String> {
                apiUrl("api/datasetsvc/public/api/v4/datasets/'$namespace'.'$name'", userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer(), json)
                    .also(success)
        } catch (e: Exception) {
            failure(e)
        }
    }

    suspend fun getDatasets(success: (List<DataSet>) -> Unit, failure: (Throwable?) -> Unit) {
        try {
            val json = client.get<String> {
                apiUrl("api/datasetsvc/public/api/v4/datasets", userAuth)
            }
            Json.nonstrict.parse(DataSet.serializer().list, json)
                    .also(success)
        } catch (e: Exception) {
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