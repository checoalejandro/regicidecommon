package com.oracle.regicidecommon.login.repositories

import com.oracle.regicidecommon.base.debug
import com.oracle.regicidecommon.base.error
import com.oracle.regicidecommon.login.data.LoginApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

class LoginRepository (val loginApi: LoginApi) {

    private val TAG = "LoginRepository"

    private val loginChannel = ConflatedBroadcastChannel<Boolean>()

    suspend fun login(username: String, password: String, language: String = "en"): ReceiveChannel<Boolean> {
        val channel = ConflatedBroadcastChannel<Boolean>()
        val login = loginApi.basicLogin(username, password, language)
        if (login) {
            debug(TAG, "Successful login!")
        } else {
            error(TAG, "Unsuccessful login")
        }
        channel.offer(login)
        return channel.openSubscription()
    }
}