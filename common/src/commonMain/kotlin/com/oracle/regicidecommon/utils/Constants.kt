package com.oracle.regicidecommon.utils

import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

object RequestPaths {

    val OAC_BASIC_LOGIN = "bi-security-login/login"
    val OAC_POST_MULTISEARCH = "ui/dv/ui/api/v1/multisearch"
    val OAC_GET_DATASET = "api/datasetsvc/public/api/v4/datasets/"
    val OAC_GET_DATASETS = "api/datasetsvc/public/api/v4/datasets"
    val OAC_GET_CANONICAL_DATA = "api/datasetsvc/public/api/v4/datasets/"

}

object LoginParamKeys {
    val PARAM_USERNAME = "j_username"
    val PARAM_PASSWORD = "j_password"
    val PARAM_SERVICENAME = "j_servicename"
    val PARAM_IDENTITYDOMAIN = "j_identitydomain"
    val PARAM_LANGUAGE = "j_language"
    val PARAM_INTERNAL = "j_internal"
    val PARAM_PROFILE_MUST = "j_profile_must"
}

object LoginParamValues {
    val VAL_USERNAME = ""
    val VAL_PASSWORD = ""
    val VAL_SERVICENAME = ""
    val VAL_IDENTITYDOMAIN = ""
    val VAL_LANGUAGE = "en"
    val VAL_INTERNAL = "true"
    val VAL_PROFILE_MUST = "true"
}

object LoginParams {
    fun get(): MutableMap<String, String> {
        val mutableMap = mutableMapOf<String, String>()
        mutableMap[LoginParamKeys.PARAM_USERNAME] = LoginParamValues.VAL_USERNAME
        mutableMap[LoginParamKeys.PARAM_PASSWORD] = LoginParamValues.VAL_PASSWORD
        mutableMap[LoginParamKeys.PARAM_SERVICENAME] = LoginParamValues.VAL_SERVICENAME
        mutableMap[LoginParamKeys.PARAM_IDENTITYDOMAIN] = LoginParamValues.VAL_IDENTITYDOMAIN
        mutableMap[LoginParamKeys.PARAM_LANGUAGE] = LoginParamValues.VAL_LANGUAGE
        mutableMap[LoginParamKeys.PARAM_INTERNAL] = LoginParamValues.VAL_INTERNAL
        mutableMap[LoginParamKeys.PARAM_PROFILE_MUST] = LoginParamValues.VAL_PROFILE_MUST
        return mutableMap
    }
}

fun MutableMap<String, String>.toFormDataContent(): FormDataContent {
    return FormDataContent(Parameters.build {
        forEach { append(it.key, it.value) }
    })
}

fun MutableMap<String, String>.toString(): String {
    return map { "${it.key} => ${it.value}" }.joinToString(", ")

}