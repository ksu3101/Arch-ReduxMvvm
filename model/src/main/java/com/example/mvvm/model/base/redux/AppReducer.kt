package com.example.mvvm.model.base.redux

import org.koin.core.KoinComponent

/**
 * @author beemo
 * @since 2020-02-24
 */
class AppReducer(
    override val initializeState: AppState
) : Reducer<AppState>, KoinComponent {

    override fun reduce(oldState: AppState, resultAction: Action): AppState {
        return reduces<State, Reducer<State>>(oldState, resultAction)
    }

    private inline fun <reified S: State, reified R: Reducer<S>> reduces(
        oldState: AppState,
        resultAction: Action
    ): AppState {
        val states = mutableMapOf<String, S>()
        getReducers<S, R>().map {
            val reducerName = it.javaClass.simpleName
            states.put(
                reducerName,
                it.reduce(oldState.getStateBy(reducerName) ?: it.initializeState, resultAction)
            )
        }
        return AppState(states)
    }

    private inline fun <reified S:State, reified R:Reducer<S>> getReducers(): List<R> =
        getKoin().get()

}