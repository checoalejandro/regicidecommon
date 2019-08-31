package com.oracle.regicidecommon.login.viewmodels

import com.oracle.regicidecommon.base.*
import com.oracle.regicidecommon.coreCommon
import com.oracle.regicidecommon.login.repositories.LoginRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.erased.instance

class LoginViewModel : BaseViewModel<LoginCoordinator, LoginState>(), LoginActions {

    private val TAG = "LoginViewModel"

    private val loginRepository: LoginRepository by coreCommon.kodein.instance()

    override fun login(username: String, password: String, language: String) {
        debug(TAG, "Starting login...")
        launch {
            loginRepository.login(username, password, language)
                .consumeEach { result ->
                    stateChannel.mutate { it.copy(successful = result) }
                }
        }
    }

    override fun getInitialState(): LoginState {
        return LoginState(false)
    }

}

data class LoginState(val successful: Boolean) : State

interface LoginActions : Actions {
    fun login(username: String, password: String, language: String = "en")
}

interface LoginCoordinator : Coordinator {
    fun showSearch()
}