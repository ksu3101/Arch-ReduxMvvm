package com.example.mvvm.arch_reduxmvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.mvvm.model.base.BaseViewModel
import com.example.mvvm.model.base.exts.canHandleStateType
import com.example.mvvm.model.base.helper.NavigationHelper
import com.example.mvvm.model.base.redux.AppStore
import com.example.mvvm.model.base.redux.State
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * @author beemo
 * @since 2020-03-02
 */
abstract class BaseFragment<S: State>: Fragment() {
    protected val appStore: AppStore by inject()
    protected val navigationHelper: NavigationHelper<S> by inject()
    private lateinit var binder: ViewDataBinding
    private val compositeDisposable = CompositeDisposable()

    protected abstract val vm: BaseViewModel<S>

    @LayoutRes
    abstract fun getLayoutResId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        compositeDisposable.clear()
        compositeDisposable.add(
            appStore.stateListener()
                .flatMap { Observable.fromIterable(it.states.values) }
                .distinctUntilChanged()
                .canHandleStateType()
                .subscribe {
                    if (it as? S == null) throw IllegalStateException("$it is not allowed state.")
                    if (!vm.render(it)) {
                        navigationHelper.handle(it)
                    }
                }
        )
        binder = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        //binder.setVariable(BR.vm, vm)
        binder.lifecycleOwner = viewLifecycleOwner
        return binder.root
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        vm.dispose()
    }

}
