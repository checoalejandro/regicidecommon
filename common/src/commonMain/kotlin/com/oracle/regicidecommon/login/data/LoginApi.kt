package com.oracle.regicidecommon.login.data

import com.oracle.regicidecommon.base.debug
import com.oracle.regicidecommon.base.error
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.utils.LoginParamKeys.PARAM_LANGUAGE
import com.oracle.regicidecommon.utils.LoginParamKeys.PARAM_PASSWORD
import com.oracle.regicidecommon.utils.LoginParamKeys.PARAM_USERNAME
import com.oracle.regicidecommon.utils.LoginParams
import com.oracle.regicidecommon.utils.RequestPaths.OAC_BASIC_LOGIN
import com.oracle.regicidecommon.utils.RequestPaths.OAC_POST_MULTISEARCH
import com.oracle.regicidecommon.utils.toFormDataContent
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.cookies.cookies
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.post
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.takeFrom
import org.kodein.di.erased.instance

class LoginApi(val endPoint: String) {

    private val cookieStorage: AcceptAllCookiesStorage by coreCommon.kodein.instance()

    private val TAG = "LoginApi"

    private val client = HttpClient {
        install(JsonFeature)
        install(HttpCookies) {
            storage = cookieStorage
        }
    }

    suspend fun basicLogin(username: String, password: String, language: String = "en"): Boolean {
        return try {
            debug(TAG, "Doing login request...")
            val params = LoginParams.get()
            params[PARAM_USERNAME] = username
            params[PARAM_PASSWORD] = password
            params[PARAM_LANGUAGE] = language

            val login = client.post<String> {
                url {
                    takeFrom(endPoint)
                    encodedPath = OAC_BASIC_LOGIN
                }
                body = params.toFormDataContent()
            }

            debug(TAG, "Login response received: $login")
            debug(TAG, "Cookies:\n ${client.cookies(endPoint)}")
            getSearch()
            login.isEmpty()
        } catch (e: Throwable) {
            error(TAG, "Couldn't login: ${e.message}")
            false
        }
    }

    suspend fun getSearch() {
        return try {
            debug(TAG, "Requesting empty search...")
            val result = client.post<String> {
                url {
                    takeFrom(endPoint)
                    encodedPath = OAC_POST_MULTISEARCH
                }
                body = TextContent(searchContent, ContentType.Application.Json)
            }
            debug(TAG, "getSearch response:\n$result")
        } catch (e: Throwable) {
            error(TAG, "Error getting search: ${e.message}")
        }
    }
}

val searchContent =
    "{\"searchQueries\":[{\"types\":[\"project\",\"folder\",\"dashboardpage\",\"dashboard\",\"report\",\"xdo\",\"dataset\",\"subjectarea\",\"replication\",\"connection\",\"dataflow\",\"sequence\",\"model\"],\"searchAttributes\":[],\"searchText\":\"*\",\"searchInFolder\":\"RECENT\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":16},{\"types\":[\"project\",\"folder\",\"dashboardpage\",\"dashboard\",\"report\",\"xdo\",\"dataset\",\"subjectarea\",\"replication\",\"connection\",\"dataflow\",\"sequence\",\"model\"],\"searchAttributes\":[],\"searchText\":\"*\",\"searchInFolder\":\"FAVORITES\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":8},{\"types\":[\"project\"],\"searchAttributes\":[],\"searchText\":\"*\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":8},{\"types\":[\"report\",\"dashboardpage\",\"xdo\"],\"searchAttributes\":[],\"searchText\":\"*\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":8},{\"types\":[\"dataset\",\"subjectarea\"],\"searchAttributes\":[],\"searchText\":\"*\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":24},{\"types\":[\"dataflow\",\"sequence\"],\"searchAttributes\":[\"replication=false\"],\"searchText\":\"*\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":24},{\"types\":[\"model\"],\"searchAttributes\":[],\"searchText\":\"*\",\"sortBy\":\"LAST_MODIFIED_DESCENDING\",\"pageSize\":24}]}"