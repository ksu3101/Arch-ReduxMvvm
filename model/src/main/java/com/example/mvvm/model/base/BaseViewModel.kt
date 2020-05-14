package com.example.mvvm.model.base

import androidx.lifecycle.ViewModel
import com.example.mvvm.model.base.redux.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author beemo
 * @since 2020-03-02
 */
abstract class BaseViewModel<S: State> : ViewModel(), RxDisposer {
    private lateinit var compositeDisposable: CompositeDisposable

    /**
     * AppStore 에서 최신 상태의 State를 받아 이를 핸들링 한다.
     *
     * @return true 일 경우 발행된 State 를 이 ViewModel 에서 핸들링 후 소모 한다.
     * false 일 경우 발행된 State 를 ViewModel 에서 핸들링 하며 Activity 에서도 핸들링 할 수 있게
     * 소모되지 않는다.
     */
    abstract fun render(state: S): Boolean

    override fun addDisposer(disposable: Disposable) {
        if (!::compositeDisposable.isInitialized) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable.add(disposable)
    }

    override fun dispose() {
        if (::compositeDisposable.isInitialized) {
            compositeDisposable.dispose()
        }
    }
}
