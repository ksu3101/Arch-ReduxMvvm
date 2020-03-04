package com.example.mvvm.model.base.redux

import io.reactivex.subjects.PublishSubject

/**
 * @author beemo
 * @since 2020-03-04
 */
class ActionProcessorMiddleWare<S: State>(
    val actionProcessor: ActionProcessor<S>
): MiddleWare<S> {
    override fun create(store: Store<S>, next: Dispatcher): Dispatcher {
        val actionEmitter: PublishSubject<Action> = PublishSubject.create()
        val disposable = actionProcessor.run(actionEmitter, store).subscribe {
            store.dispatch(it)
        }
        return { action: Action ->
            next(action)
            actionEmitter.onNext(action)
        }
    }
}