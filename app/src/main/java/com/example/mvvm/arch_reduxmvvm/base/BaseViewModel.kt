package com.example.mvvm.arch_reduxmvvm.base

import androidx.lifecycle.ViewModel
import com.example.mvvm.model.base.redux.State

/**
 * @author beemo
 * @since 2020-02-24
 */
abstract class BaseLifecycleOwnViewModel<S: State> : ViewModel()
//, RxDisposer
{
    abstract fun render(state: S): Boolean
}
