package com.example.mvvm.model.base

import io.reactivex.disposables.Disposable

/**
 * @author beemo
 * @since 2020-03-02
 */
interface RxDisposer {
    fun addDisposer(disposable: Disposable)

    fun dispose()
}