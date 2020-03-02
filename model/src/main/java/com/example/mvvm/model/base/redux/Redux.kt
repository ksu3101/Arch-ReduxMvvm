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
    /**
     * 저장된 State 를 가져온다.
     */
    fun getState(): S?

    /**
     */
    fun dispatch(action: Action)

    /**
     * 발행되는 State 에 대해서 구독한다.
     * 원래 `subscribe()` 의 이름을 가져야 하나 Rx 의 동일한 이름을 가진 함수가 있어
     * 원할한 구분을 위해 이름을 변경 하였음.
     */
    fun stateListener(): Observable<S>
}

interface MiddleWare<S: State> {
    fun create(store: Store<S>, next: Dispatcher): Dispatcher
}
