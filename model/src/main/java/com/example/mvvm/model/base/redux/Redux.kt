package com.example.mvvm.model.base.redux

import io.reactivex.Observable

/**
 * @author beemo
 * @since 2020-02-24
 */

interface Action

interface State

interface Reducer<S : State> {
    fun reduce(oldState: S, resultAction: Action): S
}

typealias Dispatcher = (Action) -> Unit

interface Store<S: State> {
    fun getState(): S
    fun dispatch(action: Action)
    fun subscribe(): Observable<S>
}

interface MiddleWare<S: State> {
    fun create(store: Store<S>, next: Dispatcher): Dispatcher
}
