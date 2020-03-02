package com.example.mvvm.model.domain.common

import com.example.mvvm.model.base.redux.Action
import com.example.mvvm.model.base.redux.Reducer

/**
 * @author beemo
 * @since 2020-03-02
 */
class MessageReducer: Reducer<MessageState> {

    override fun reduce(oldState: MessageState, resultAction: Action): MessageState {
        return when(resultAction) {
            is HandledMessageAction -> HandledMessageState
            is ShowingGeneralToast -> ShowingGeneralToastState(resultAction.messageResId)
            is ShowingErrorToast -> ShowingErrorToastState(
                resultAction.errorMessageResId,
                resultAction.errorMessageStr
            )
            else -> oldState
        }
    }

}