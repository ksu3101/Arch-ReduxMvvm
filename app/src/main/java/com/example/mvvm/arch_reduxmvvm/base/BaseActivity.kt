package com.example.mvvm.arch_reduxmvvm.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.mvvm.common.KOIN_CURRENT_ACTIVITY
import com.example.mvvm.model.base.helper.MessageHelper
import com.example.mvvm.model.base.redux.AppStore
import com.example.mvvm.model.domain.common.HandledMessageAction
import com.example.mvvm.model.domain.common.MessageState
import com.example.mvvm.model.domain.common.ShowingErrorToastState
import com.example.mvvm.model.domain.common.ShowingGeneralToastState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

/**
 * @author beemo
 * @since 2020-03-02
 */
abstract class BaseActivity : AppCompatActivity() {
    protected val messageHelper: MessageHelper by inject()
    protected val appStore: AppStore by inject()
    private lateinit var compositeDisposable: CompositeDisposable

    @LayoutRes
    abstract fun getLayoutResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getKoin().setProperty(KOIN_CURRENT_ACTIVITY, this)
        setContentView(getLayoutResId())
    }

    override fun onResume() {
        super.onResume()
        subscribeMessageState()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::compositeDisposable.isInitialized) {
            compositeDisposable.dispose()
        }
    }

    private fun subscribeMessageState() {
        compositeDisposable.add(
            appStore.stateListener()
                .flatMap { Observable.fromArray(it.states) }
                .ofType(MessageState::class.java)
                .doOnNext{ appStore.dispatch(HandledMessageAction) }
                .subscribe { handleMessageState(it) }
        )
    }

    private fun handleMessageState(messageState: MessageState) {
        when(messageState) {
            is ShowingGeneralToastState -> {
                messageHelper.showingGeneralToast(messageState.messageResId)
            }
            is ShowingErrorToastState -> {
                messageHelper.showingErrorToast(
                    messageState.errorMessageResId,
                    messageState.errorMessageStr
                )
            }
            // 추가 공통 메시지 핸들링은 여기에 추가 한다.
        }
    }

}