package com.example.mvvm.model.base.redux

import io.reactivex.Observable

/**
 * @author beemo
 * @since 2020-03-04
 */
interface ActionProcessor<S : State> {
    fun run(action: Observable<Action>, store: Store<S>): Observable<out Action>
}

/**
 * wrapper class of multiple action processors.
 */
class CombinedActionProcessor<S: State>(
    private val actionProcessrs: Iterable<ActionProcessor<S>>
): ActionProcessor<S> {
    override fun run(action: Observable<Action>, store: Store<S>): Observable<out Action> {
        return Observable.fromIterable(actionProcessrs)
            .flatMap { it.run(action, store) }
    }
}