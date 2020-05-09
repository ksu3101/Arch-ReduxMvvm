package com.example.mvvm.model.base.exts

import com.example.mvvm.model.base.RxDisposer
import com.example.mvvm.model.base.redux.Action
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * @author beemo
 * @since 2020-03-04
 */

private typealias OnNext<T> = ((T) -> (Unit))

private typealias OnNextAnd<T> = ((T) -> (Any?))
private typealias OnError = ((Throwable) -> Unit)?
private typealias OnComplete = (() -> Unit)?

fun <T> Observable<T>.subscribeWith(
    rxDisposer: RxDisposer,
    onNext: OnNext<T>,
    onError: OnError = null,
    onComplete: OnComplete = null
) {
    rxDisposer.addDisposer(
        this.subscribeOn(AndroidSchedulers.mainThread())
            .run {
                if (onError != null && onComplete != null) {
                    this.subscribe(onNext, onError, onComplete)
                } else if (onError != null) {
                    this.subscribe(onNext, onError)
                } else {
                    this.subscribe(onNext)
                }
            }
    )
}

fun <T> Flowable<T>.subscribeWith(
    rxDisposer: RxDisposer,
    onNext: OnNext<T>,
    onError: OnError = null,
    onComplete: OnComplete = null
) {
    rxDisposer.addDisposer(
        this.subscribeOn(AndroidSchedulers.mainThread())
            .run {
                if (onError != null && onComplete != null) {
                    this.subscribe(onNext, onError, onComplete)
                } else if (onError != null) {
                    this.subscribe(onNext, onError)
                } else {
                    this.subscribe(onNext)
                }
            }
    )
}

fun <T> Maybe<T>.subscribeWith(
    rxDisposer: RxDisposer,
    onNext: OnNext<T>,
    onError: OnError = null,
    onComplete: OnComplete = null
) {
    rxDisposer.addDisposer(
        this.subscribeOn(AndroidSchedulers.mainThread())
            .run {
                if (onError != null && onComplete != null) {
                    this.subscribe(onNext, onError, onComplete)
                } else if (onError != null) {
                    this.subscribe(onNext, onError)
                } else {
                    this.subscribe(onNext)
                }
            }
    )
}

fun <T> Single<T>.subscribeWith(
    rxDisposer: RxDisposer,
    onNext: OnNext<T>,
    onError: OnError = null
) {
    rxDisposer.addDisposer(
        this.subscribeOn(AndroidSchedulers.mainThread())
            .run {
                if (onError != null) {
                    this.subscribe(onNext, onError)
                } else {
                    this.subscribe(onNext)
                }
            }
    )
}

fun Completable.subscribeWith(
    rxDisposer: RxDisposer,
    onComplete: OnComplete = null,
    onError: OnError = null
) {
    rxDisposer.addDisposer(
        this.subscribeOn(AndroidSchedulers.mainThread())
            .run {
                if (onError != null) {
                    this.subscribe(onComplete, onError)
                } else {
                    this.subscribe(onComplete)
                }
            }
    )
}

inline fun createActionProcessor(crossinline merger: (Observable<Action>) -> Array<Observable<Action>>): ObservableTransformer<Action, Action> =
    ObservableTransformer {
        it.publish { shared ->
            Observable.mergeArray<Action>(*merger(shared))
        }
    }

inline fun <T : Action> actionTransformer(crossinline body: (T) -> Observable<Action>): ObservableTransformer<T, Action> {
    return ObservableTransformer { actionObservable ->
        actionObservable.flatMap {
            body(it)
        }
    }
}