package com.example.mvvm.model.base.redux

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import org.koin.core.KoinComponent

/**
 * @author beemo
 * @since 2020-02-24
 */
class AppStore(
    val initializeAppState: AppState,
    var reducer: Reducer<AppState>
) : Store<AppState>, KoinComponent {
    private val stateEmitter: BehaviorSubject<AppState> = BehaviorSubject.create()
    private val middleWares: Array<MiddleWare<AppState>> = getKoin().get()
    private var appState: AppState = initializeAppState

    private var dispatcher: Dispatcher

    init {
        dispatcher = middleWares.foldRight({ dispatchedAction ->
            appState = reducer.reduce(appState, dispatchedAction)
            stateEmitter.onNext(appState)
        }) { middleWare, next ->
            middleWare.create(this, next)
        }
    }

    override fun dispatch(action: Action) {
        dispatcher(action)
    }

    override fun subscribe(): Observable<AppState> =
        stateEmitter.hide().observeOn(AndroidSchedulers.mainThread())

    // todo : is need unsubscribe function??

    override fun getState(): AppState = appState
}