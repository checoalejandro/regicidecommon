package com.oracle.regicidecommon.base

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.SharedImmutable
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<CD: Coordinator, ST: State>: Actions {
    /**
     * The initial empty [State] of the screen.
     */
    abstract fun getInitialState(): ST

    open fun returnInitialState() = true

    /**
     * A [ConflatedBroadcastChannel] used for preserving and offering the latest screen [State] to the [stateChangeListener].
     */
    protected val stateChannel: ConflatedBroadcastChannel<ST> = ConflatedBroadcastChannel(getInitialState())

    /**
     * A [WeakRef] value pointing to the [StateChangeListener] being used to display the current [State].
     */
    @SharedImmutable
    protected var stateChangeListener: WeakRef<StateChangeListener<ST>>? = null

    /**
     * A [WeakRef] value pointing to the [Coordinator] being used to handle navigation on the current [StateChangeListener].
     */
    protected var coordinator: WeakRef<CD>? = null

    private var isInitializing = true

    private val job = Job()
    private val coroutineContext: CoroutineContext
        get() = MainDispatcher + job

    private val heavyContext: CoroutineContext
        get() = heavyDispatcher + job

    private val parsingContext: CoroutineContext
        get() = parsingDispatcher + job

    private val dbContext: CoroutineContext
        get() = dbDispatcher + job

    private val defaultScope = CoroutineScope(coroutineContext)
    private val heavyScope = CoroutineScope(heavyContext)
    private val parsingScope = CoroutineScope(parsingContext)
    private val dbScope = CoroutineScope(dbContext)

    init {
        debug(TAG, "starting init block")
        launch {
            // Immediately start listening for State changes.
            stateChannel.openSubscription().consumeEach {
                // New state has been received, notify the view of changes.
                debug(TAG, "Updated state $it")
                if (!returnInitialState() && isInitializing) {
                    isInitializing = false
                    return@consumeEach
                }
                debug(TAG, "Sending new value to main thread")
                launch(MainDispatcher) {
                    debug(TAG, "value received and will be called")
                    stateChangeListener?.value?.onStateChange(it)
                }
            }
        }
    }

    //region ViewModel callback
    /**
     * Callback called when the [BaseViewModel]'s associated View is in the foreground.
     */
    open fun onActive() {

    }

    /**
     * Callback called when the [BaseViewModel]'s associated View goes in the background.
     */
    open fun onInactive() {

    }

    /**
     * Callback called when the [BaseViewModel] is detached from the associated View.
     */
    open fun onDestroy() {
        job.cancel()
    }
    //endregion

    /**
     * Set the associated [coordinator] in order to receive navigation callbacks.
     */
    fun setCoordinator(coordinator: CD) {
        this.coordinator = buildWeakRef(coordinator)
    }

    /**
     * Set the associated [stateChangeListener] in order to receive changes to [State].
     */
    fun setStateChangeListener(stateChangeListener: StateChangeListener<ST>) {
        this.stateChangeListener = buildWeakRef(stateChangeListener)
    }

    /**
     * Do a [task] asynchronously and then [parse] the result.
     */
    fun <T> doAsync(task: suspend () -> T, parse: (T) -> Unit = {}) {
        launch {
            val result = withContext(context = Dispatchers.Unconfined) {
                task.invoke()
            }
            parse(result)
        }
    }

    /**
     * Mutate the existing [ConflatedBroadcastChannel.value], modifying some parts of the state and
     * call [ConflatedBroadcastChannel.offer] with the new mutated value.
     */
    fun ConflatedBroadcastChannel<ST>.mutate(mutation: (value: ST) -> ST) {
        stateChannel.offer(mutation.invoke(value))
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) = defaultScope.launch(MainDispatcher, CoroutineStart.DEFAULT, block)

    fun heavyLaunch(block: suspend CoroutineScope.() -> Unit) = heavyScope.heavyLaunch(CoroutineStart.DEFAULT, block)

    fun parserLaunch(block: suspend CoroutineScope.() -> Unit) = parsingScope.parsingLaunch(CoroutineStart.DEFAULT, block)

    fun dbLaunch(block: suspend CoroutineScope.() -> Unit) = dbScope.dbLaunch(CoroutineStart.DEFAULT, block)

    companion object {
        val TAG = BaseViewModel::class.simpleName
    }
}

/**
 * Each View can have additional [Actions] that the user can perform on that particular screen,
 * ie. refreshing list, clicking a button.
 */
interface Actions

/**
 * Each View can have an associated [Coordinator] that manages navigation from the current screen.
 */
interface Coordinator

/**
 * Each View has an associated [State] that represents every piece of information on the screen,
 * ie. a list of items, a search query.
 */
interface State {
    var s: String
}

/**
 * State interface implemented by each View.
 */
interface StateChangeListener<ST : State> {
    fun onStateChange(state: ST)
}

fun CoroutineScope.heavyLaunch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(heavyDispatcher, start, block)
}

fun CoroutineScope.parsingLaunch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(parsingDispatcher, start, block)
}

fun CoroutineScope.dbLaunch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(dbDispatcher, start, block)
}