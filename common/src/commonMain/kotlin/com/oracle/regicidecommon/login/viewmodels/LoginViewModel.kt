package com.oracle.regicidecommon.login.viewmodels

import com.oracle.regicidecommon.base.*
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.login.repositories.LoginRepository
import com.oracle.regicidecommon.models.Host
import com.oracle.regicidecommon.models.User
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import org.kodein.di.erased.instance

@ExperimentalCoroutinesApi
class LoginViewModel : BaseViewModel<LoginCoordinator, LoginState>(), LoginActions {

    private val TAG = "LoginViewModel"

    private val loginRepository: LoginRepository by coreCommon.kodein.instance()

    override fun login(host: Host, username: String, password: String, language: String) {
        debug(TAG, "Starting login...")
        launch {
            loginRepository.login(host, username, password, language)
                .consumeEach { result ->
                    stateChannel.mutate {
                        it.copy(
                            s = "login",
                            successful = result
                        )
                    }
                }
        }
    }

    override fun getDefaultUser() {
        debug(TAG, "Checking if default user...")
        dbLaunch {
            loginRepository.getDefaultUser()
                .consumeEach { user ->
                    stateChannel.mutate {
                        it.copy(
                            s = "getDefaultUser",
                            defaultUser = user
                        )
                    }
                }
        }
    }

    override fun getInitialState(): LoginState {
        return LoginState(false)
    }

    override fun getHosts() {
        debug(TAG, "Getting host list...")
        dbLaunch {
            loginRepository.getHosts()
                .consumeEach { hosts ->
                    stateChannel.mutate {
                        it.copy(
                            s = "getHosts",
                            hosts = hosts
                        )
                    }
                }
        }
    }

    override fun getUsersByHost(host: String) {
        debug(TAG, "Getting users list by host...")
        dbLaunch {
            loginRepository.getUsersByHost(host)
                .consumeEach { users ->
                    stateChannel.mutate {
                        it.copy(
                            s = "getUsersByHost",
                            usersByHost = users
                        )
                    }
                }
        }
    }

}

data class LoginState(
    val successful: Boolean = false,
    val hasDefaultUser: Boolean = false,
    val defaultUser: User? = null,
    val hosts: List<Host> = emptyList(),
    override var s: String = "",
    val usersByHost: List<User> = emptyList()
) : State

interface LoginActions : Actions {
    fun login(host: Host, username: String, password: String, language: String = "en")
    fun getHosts()
    fun getDefaultUser()
    fun getUsersByHost(host: String)

}

interface LoginCoordinator : Coordinator {
    fun showSearch()
}