package com.example.mvvm.model.base.redux

import org.koin.core.KoinComponent

/**
 * @author beemo
 * @since 2020-02-24
 */
class AppReducer : Reducer<AppState>, KoinComponent {

    override fun reduce(oldState: AppState, resultAction: Action): AppState {
        return reduces<State, Reducer<State>>(oldState, resultAction)
    }

    private inline fun <reified S: State, reified R: Reducer<S>> reduces(
        oldState: S,
        resultAction: Action
    ): AppState {
        return AppState(
            *(getReducers<S, R>().map  { it.reduce(oldState, resultAction) }.toTypedArray())
        )
    }

    private inline fun <reified S:State, reified R:Reducer<S>> getReducers(): List<R> =
        getKoin().get()

}