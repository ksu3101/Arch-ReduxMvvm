package com.example.mvvm.model.base.exts

import com.example.mvvm.model.base.redux.State
import io.reactivex.Observable

/**
 * @author beemo
 * @since 2020-03-02
 */

inline fun <reified S: State> Observable<S>.isStateType(): Observable<S> =
    ofType<S>(S::class.java)