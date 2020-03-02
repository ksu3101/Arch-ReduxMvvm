package com.example.mvvm.model.base.helper

import com.example.mvvm.model.base.redux.State

/**
 * @author beemo
 * @since 2020-03-02
 */
interface NavigationHelper<S: State> {

    fun handle(state: S)

}