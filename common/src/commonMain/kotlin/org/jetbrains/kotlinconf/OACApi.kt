package org.jetbrains.kotlinconf

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.kotlinconf.models.DataSet
import org.jetbrains.kotlinconf.models.EmptyList

class OACApi(val endPoint: String, val userId: String) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict).apply {
                setMapper(DataSet::class, DataSet.serializer())
                setMapper(EmptyList::class, EmptyList.serializer())
            }
        }
    }

    suspend fun getDatasets(): List<DataSet> = client.get {
        apiUrl("api/datasetsvc/public/api/v4/datasets", userId)
    }

    suspend fun getDataset(namespace: String, name: String): DataSet = client.get {
        apiUrl("api/datasetsvc/public/api/v4/datasets/'$namespace'.'$name'", userId)
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String, userId: String) {
        header(HttpHeaders.Authorization, "Basic $userId")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}

class IgnoreOutgoingContentJsonSerializer(private val delegate: JsonSerializer):JsonSerializer by delegate {
    override fun write(data: Any): OutgoingContent {
        if (data is OutgoingContent) {
            return data
        }
        return delegate.write(data)
    }
}

fun JsonSerializer.ignoreOutgoingContent() = IgnoreOutgoingContentJsonSerializer(this)