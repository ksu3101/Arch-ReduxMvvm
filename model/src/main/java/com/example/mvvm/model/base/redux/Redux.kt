package com.example.mvvm.model.base.redux

import android.util.Log
import com.example.mvvm.common.LOG_TAG
import com.example.mvvm.common.exts.getSuperClassNames
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.KoinComponent

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

// - - - -
