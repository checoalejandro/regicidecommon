package com.oracle.regicidecommon.login.repositories

import com.oracle.regicidecommon.base.debug
import com.oracle.regicidecommon.base.error
import com.oracle.regicidecommon.login.data.LoginApi
import com.oracle.regicidecommon.models.Host
import com.oracle.regicidecommon.models.User
import com.oracle.regicidecommon.oac.data.HostDao
import com.oracle.regicidecommon.oac.data.UserDao
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

class LoginRepository(
    private val loginApi: LoginApi,
    private val hostDao: HostDao,
    private val userDao: UserDao
) {

    private val TAG = "LoginRepository"

    private val loginChannel = ConflatedBroadcastChannel<Boolean>()

    suspend fun login(
        host: Host,
        username: String,
        password: String,
        language: String = "en"
    ): ReceiveChannel<Boolean> {
        val channel = ConflatedBroadcastChannel<Boolean>()
        val login = loginApi.basicLogin(username, password, language)
        if (login) {
            val user = User(host.host, username, password)
            userDao.insertDefaultUser(user)
            debug(TAG, "Successful login!")
        } else {
            hostDao.insertHost(host.host, host.port, host.ssl)
            error(TAG, "Unsuccessful login")
        }
        channel.offer(login)
        return channel.openSubscription()
    }

    suspend fun getHosts(): ReceiveChannel<List<Host>> {
        val channel = ConflatedBroadcastChannel<List<Host>>()
        val hosts = hostDao.getHosts() ?: emptyList()
        channel.offer(hosts)
        return channel.openSubscription()
    }

    suspend fun getUsersByHost(host: String): ReceiveChannel<List<User>> {
        val channel = ConflatedBroadcastChannel<List<User>>()
        val users = userDao.getUsersFromHost(host)
        channel.offer(users)
        return channel.openSubscription()
    }

    suspend fun getDefaultUser(): ReceiveChannel<User?> {
        val channel = ConflatedBroadcastChannel<User?>()
        channel.offer(userDao.getDefaultUser())
        return channel.openSubscription()
    }
}